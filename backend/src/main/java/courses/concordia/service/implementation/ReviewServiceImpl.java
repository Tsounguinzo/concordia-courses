package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.dto.model.CommentDto;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;
import courses.concordia.dto.response.ProcessingResult;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.*;
import courses.concordia.repository.*;
import courses.concordia.service.ReviewService;
import courses.concordia.service.TokenBlacklistService;
import courses.concordia.util.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CommentRepository commentRepository;
    private final ResourceLinkRepository resourceLinkRepository;
    private final TokenBlacklistService blacklistService;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;
    private static final Map<String, Instructor.Course> courseMap = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            InputStream is = getClass().getResourceAsStream("/subject-catalogs.json");
            if (is == null) {
                throw new IllegalStateException("Resource subject-catalogs.json not found");
            }
            Map<String, List<String>> coursesData = JsonUtils.getData(is, new TypeToken<Map<String, List<String>>>() {
            });
            if (coursesData != null) {
                coursesData.forEach((key, values) -> values.forEach(value -> {
                    String courseKey = key + value;
                    Instructor.Course course = new Instructor.Course(key, value, "concordia-university");
                    courseMap.put(courseKey, course);
                }));
            }
        } catch (Exception e) {
            log.error("Failed to initialize course data", e);
            throw new RuntimeException("Failed to initialize course data", e);
        }
    }

    /**
     * Adds or updates a review based on the provided ReviewDto.
     * If the review exists, it updates; otherwise, it creates a new review.
     *
     * @param reviewDto The review data transfer object containing review details.
     * @return The added or updated review data transfer object.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", allEntries = true, condition = "#reviewDto.type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", allEntries = true, condition = "#reviewDto.type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true, condition = "#reviewDto.type.equals('course')"),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true, condition = "#reviewDto.type.equals('instructor')"),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCache", allEntries = true),
            @CacheEvict(value = "courseInstructorsCache", key = "#reviewDto.courseId")
    })
    @Transactional
    @Override
    public ReviewDto addOrUpdateReview(ReviewDto reviewDto) {
        checkBlacklistedUser(reviewDto.getUserId());

        Review review;
        boolean isNewReview;

        if (reviewDto.getType() != null) {
            if (reviewDto.getType().equals("instructor")) {
                Optional<Review> existingReviewOpt = reviewRepository
                        .findByInstructorIdAndUserIdAndType(reviewDto.getInstructorId(), reviewDto.getUserId(), reviewDto.getType());
                isNewReview = existingReviewOpt.isEmpty();
                review = existingReviewOpt
                        .map(r -> updateReviewFromDto(r, reviewDto))
                        .orElseGet(() -> createReviewFromDto(reviewDto));
            } else {
                Optional<Review> existingReviewOpt = reviewRepository
                        .findByCourseIdAndUserIdAndType(reviewDto.getCourseId(), reviewDto.getUserId(), reviewDto.getType());
                isNewReview = existingReviewOpt.isEmpty();
                review = existingReviewOpt
                        .map(r -> updateReviewFromDto(r, reviewDto))
                        .orElseGet(() -> createReviewFromDto(reviewDto));
            }

            // Handle ResourceLinks
            if (reviewDto.getResourceLinks() != null) {
                List<ResourceLink> resourceLinks = reviewDto.getResourceLinks().stream()
                        .map(dto -> modelMapper.map(dto, ResourceLink.class))
                        .collect(Collectors.toList());

                if (isNewReview) {
                    // For new reviews, all resource links are new
                    List<ResourceLink> savedResourceLinks = resourceLinkRepository.saveAll(resourceLinks);
                    review.setResourceLinks(new ArrayList<>(savedResourceLinks));
                } else {
                    // For existing reviews, identify new, updated, and deleted resource links
                    List<ResourceLink> existingResourceLinks = review.getResourceLinks();
                    List<ResourceLink> updatedResourceLinks = new ArrayList<>();

                    // Process incoming resource links
                    for (ResourceLink currentLink : resourceLinks) {
                        if (currentLink.get_id() != null) {
                            // Attempt to find and update existing link
                            Optional<ResourceLink> foundLink = existingResourceLinks.stream()
                                    .filter(rl -> rl.get_id().equals(currentLink.get_id()))
                                    .findFirst();
                            if (foundLink.isPresent()) {
                                ResourceLink linkToUpdate = foundLink.get();
                                modelMapper.map(currentLink, linkToUpdate); // Update fields
                                updatedResourceLinks.add(resourceLinkRepository.save(linkToUpdate));
                            } else {
                                // ID present but not found in existing list - treat as new (or handle error)
                                currentLink.set_id(null); // Ensure it's treated as new by MongoDB
                                updatedResourceLinks.add(resourceLinkRepository.save(currentLink));
                            }
                        } else {
                            // New link (no ID)
                            updatedResourceLinks.add(resourceLinkRepository.save(currentLink));
                        }
                    }

                    // Identify and delete links that were removed
                    List<ResourceLink> linksToDelete = existingResourceLinks.stream()
                            .filter(existingLink -> resourceLinks.stream()
                                    .noneMatch(newLink -> existingLink.get_id().equals(newLink.get_id())))
                            .collect(Collectors.toList());
                    if (!linksToDelete.isEmpty()) {
                        resourceLinkRepository.deleteAll(linksToDelete);
                    }
                    review.setResourceLinks(updatedResourceLinks);
                }
            }


            review = reviewRepository.save(review);

            if (reviewDto.getType().equals("instructor")) {
                updateInstructorRatingCoursesAndTags(review.getInstructorId());
            } else {
                updateCourseExperience(review.getCourseId());
            }

            return ReviewMapper.toDto(review);
        } else {
            throw exception();
        }
    }

    /**
     * Creates a Review entity from a ReviewDto.
     *
     * @param reviewDto The review data transfer object.
     * @return A new Review entity.
     */
    private Review createReviewFromDto(ReviewDto reviewDto) {
        Review review = modelMapper.map(reviewDto, Review.class);
        // Resource links will be handled after this stage in addOrUpdateReview
        review.setResourceLinks(new ArrayList<>()); // Initialize to avoid null pointer if not set by DTO
        return review; // Not saving here, will be saved in addOrUpdateReview after resource links handling
    }

    public boolean reviewDoesNotExist(Review review) {
        return !reviewRepository.existsByTimestampAndInstructorIdAndCourseId(review.getTimestamp(), review.getInstructorId(), review.getCourseId());
    }

    /**
     * Updates an existing Review entity from a ReviewDto.
     *
     * @param existingReview The existing Review entity.
     * @param reviewDto      The review data transfer object with updated fields.
     * @return The updated Review entity.
     */
    private Review updateReviewFromDto(Review existingReview, ReviewDto reviewDto) {
        // Preserve existing resource links, they will be explicitly managed
        List<ResourceLink> currentResourceLinks = new ArrayList<>(existingReview.getResourceLinks());
        modelMapper.map(reviewDto, existingReview);
        existingReview.setResourceLinks(currentResourceLinks); // Restore after mapping
        if (reviewDto.getTags().isEmpty())
            existingReview.setTags(Collections.emptySet()); // By default, ModelMapper does not map empty collections
        // Not saving here, will be saved in addOrUpdateReview after resource links handling
        return existingReview;
    }

    /**
     * Deletes a review identified by courseId and userId.
     *
     * @param id The ID of the review.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", allEntries = true, condition = "#type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", allEntries = true, condition = "#type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCache", allEntries = true),
            @CacheEvict(value = "courseInstructorsCache", key = "#courseId")
    })
    @Transactional
    @Override
    public void deleteReview(String id, String type, String courseId, String instructorId) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty() || review.get().getType() == null) {
            throw exception(id);
        } else {
            if (review.get().getType().equals("instructor")) {
                reviewRepository.deleteById(id);
                updateInstructorRatingCoursesAndTags(review.get().getInstructorId());
            } else {
                reviewRepository.deleteById(id);
                updateCourseExperience(review.get().getCourseId());
            }
        }
    }

    /**
     * Retrieves a list of reviews for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of ReviewDto objects representing the user's reviews.
     */
    @Override
    public List<ReviewDto> getUserReviews(String userId) {
        log.info("Retrieving reviews for user with ID {}", userId);
        Query query = new Query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves reviews based on filtering criteria with pagination support.
     *
     * @param limit   The maximum number of reviews to return.
     * @param offset  The offset from where to start the query.
     * @param filters The filtering criteria for reviews.
     * @return A list of ReviewDto objects that match the filtering criteria.
     */
    @Cacheable(value = "reviewsCacheWithFilters", key = "{#limit, #offset, #filters.hashCode()}")
    @Override
    public List<ReviewDto> getReviewsWithFilter(int limit, int offset, ReviewFilterDto filters) {
        log.info("Retrieving reviews with limit {}, offset {}, and filters {}", limit, offset, filters);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Review> page = findFilteredReviews(filters, pageable);
        return page.getContent()
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a review given its id.
     *
     * @param id The ID of the review.
     * @return A ReviewDto objects representing the target reviews.
     */
    @Override
    public ReviewDto getReviewById(String id) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            return ReviewMapper.toDto(review.get());
        } else {
            log.error("Review not found with ID: {}", id);
            throw exception(id);
        }
    }

    /**
     * Uploads reviews from a file.
     *
     * @param file The file containing reviews.
     * @return A ProcessingResult object containing the result of the review upload.
     */
    @Override
    public ProcessingResult uploadReviews(MultipartFile file) {
        log.info("Starting to upload reviews from file: {}", file.getOriginalFilename());
        List<ReviewDto> reviewDtos = processReviewFile(file);

        ProcessingResult result = new ProcessingResult();

        for (ReviewDto dto : reviewDtos) {
            if (dto.getContent() == null || dto.getContent().isBlank() || !dto.getContent().matches(".*[a-zA-Z].*") || dto.getContent().matches("^[a-zA-Z0-9]{1,3}$")) {
                result.incrementFailed();
                result.addError("Review content is empty");
                continue;
            }
            try {
                Review review = modelMapper.map(dto, Review.class);

                if (reviewRepository.existsByTimestampAndInstructorIdAndCourseId(review.getTimestamp(), review.getInstructorId(), review.getCourseId())) {
                    result.incrementAlreadyExists();
                } else {
                    // New review, add it
                    if (instructorRepository.existsById(review.getInstructorId())) {
                        reviewRepository.save(review);
                        result.incrementAdded();
                    } else {
                        result.incrementFailed();
                        result.addError("Instructor with ID " + review.getInstructorId() + " does not exist");
                    }
                }

            } catch (Exception e) {
                String errorMsg = String.format("Failed to process review (instructorId=%s, courseId=%s, timestamp=%s, userId=%s): %s",
                        dto.getInstructorId(), dto.getCourseId(), dto.getTimestamp(), dto.getUserId(), e.getMessage());
                log.error(errorMsg, e);
                result.addError(errorMsg);
                result.incrementFailed();
            }
        }

        log.info("Reviews processing completed. Added: {}, AlreadyExist: {}, Failed: {}",
                result.getAddedCount(), result.getAlreadyExistsCount(), result.getFailedCount());

        if (!result.getErrors().isEmpty()) {
            log.info("There were errors during processing: {}", result.getErrors());
        }

        return result;
    }

    private List<ReviewDto> processReviewFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return JsonUtils.getData(inputStream, new TypeToken<List<ReviewDto>>() {
            });
        } catch (IOException e) {
            String errorMsg = "Failed to process review file: " + file.getOriginalFilename();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Finds and deletes duplicate reviews. A "duplicate" is defined as multiple reviews with
     * the same (courseId, content, timestamp). Keeps exactly one review per group,
     * deletes the rest.
     *
     * @return a ProcessingResult containing information about how many duplicates were deleted
     */
    @Override
    public ProcessingResult deleteDuplicateReviews() {
        ProcessingResult result = new ProcessingResult();

        List<Review> allReviews = reviewRepository.findAll();

        Map<String, List<Review>> grouped = new HashMap<>();

        for (Review review : allReviews) {
            String key = String.join(
                    "_",
                    Objects.toString(review.getCourseId(), ""),
                    Objects.toString(review.getContent(), ""),
                    Objects.toString(review.getTimestamp(), "")
            );
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(review);
        }

        List<Review> duplicates = new ArrayList<>();
        for (List<Review> group : grouped.values()) {
            if (group.size() > 1) {
                group.sort((r1, r2) -> Integer.compare(r2.getInstructorId().length(), r1.getInstructorId().length()));
                duplicates.addAll(group.subList(1, group.size()));
            }
        }

        if (!duplicates.isEmpty()) {
            reviewRepository.deleteAll(duplicates);
            result.setDeletedCount(duplicates.size());
        }

        log.info("Deleted {} duplicate reviews.", duplicates.size());

        return result;
    }

    /**
     * Deletes all reviews of type "instructor" which reference a non-existent instructorId.
     *
     * @return a ProcessingResult containing info about how many reviews were deleted
     */
    @Override
    public ProcessingResult deleteReviewsWithNonExistentInstructorIds() {
        ProcessingResult result = new ProcessingResult();

        Set<String> validInstructorIds = instructorRepository.findAll()
                .stream()
                .map(Instructor::get_id)
                .collect(Collectors.toSet());

        List<Review> instructorReviews = reviewRepository.findAllByType("instructor");

        List<Review> invalidInstructorReviews = instructorReviews.stream()
                .filter(review -> {
                    String instructorId = review.getInstructorId();
                    // If instructorId is null or not found in the validInstructorIds set
                    return instructorId == null || !validInstructorIds.contains(instructorId);
                })
                .collect(Collectors.toList());

        if (!invalidInstructorReviews.isEmpty()) {
            reviewRepository.deleteAll(invalidInstructorReviews);
            log.info("Deleted {} reviews referencing non-existent instructors.", invalidInstructorReviews.size());
            result.setDeletedCount(invalidInstructorReviews.size());
        } else {
            log.info("No reviews referencing non-existent instructors found.");
        }

        return result;
    }


    /**
     * Finds reviews based on filtering criteria with pagination support.
     *
     * @param filters  The filtering criteria for reviews.
     * @param pageable The pagination information.
     * @return A page of Review objects that match the filtering criteria.
     */
    private Page<Review> findFilteredReviews(ReviewFilterDto filters, Pageable pageable) {
        Query query = new Query().with(pageable);
        // Handle sorting
        applySorting(filters, query);

        long count = mongoTemplate.count(query, Review.class);
        List<Review> reviews = mongoTemplate.find(query, Review.class);

        log.info("Found {} courses matching filter criteria", reviews.size());

        return new PageImpl<>(reviews, pageable, count);
    }

    /**
     * Applies sorting to the query based on the provided filter criteria.
     * If no sorting criteria is specified, the default sorting is by date.
     *
     * @param filter The filter criteria for reviews.
     * @param query  The query to which sorting will be applied.
     */
    private void applySorting(ReviewFilterDto filter, Query query) {
        System.out.println("Applying sorting " + filter);
        if (filter.getSortBy() != null) {
            ReviewFilterDto.ReviewSortType sortType = filter.getSortBy();
            String sortField = "timestamp"; // Default sorting by date

            switch (sortType) {
                case Experience:
                    sortField = "experience";
                    break;
                case Likes: // Assuming you have a corresponding enum value for this
                    sortField = "likes";
                    break;
                case Date: // Explicit case for clarity, even though it's the default
                default:
                    // The default sortField is already set to "timestamp"
                    break;
            }

            log.info("Applying sort by {}", sortField);
            query.with(Sort.by(Sort.Direction.DESC, sortField));
        } else {
            log.info("No sort type specified, defaulting to sort by date");
            query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        }
    }

    /**
     * Updates the average experience and difficulty for a course based on its reviews.
     *
     * @param courseId The ID of the course.
     */
    private void updateCourseExperience(String courseId) {
        List<Review> reviews = reviewRepository.findAllByCourseId(courseId);
        double avgExperienceAndRating = reviews.stream().mapToDouble(r -> {
            if (r.getType().equals("course")) {
                return r.getExperience();
            } else {
                return r.getRating();
            }
        }).average().orElse(0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            int[][] distributions = getDistributions(reviews);
            course.setDifficultyDistribution(distributions[0]);
            course.setExperienceDistribution(distributions[1]);
            course.setAvgExperience(avgExperienceAndRating);
            course.setAvgDifficulty(avgDifficulty);
            course.setReviewCount(reviewsCount);
            courseRepository.save(course);
        }
    }

    /**
     * Updates the average rating, difficulty and tags for a prof based on its reviews.
     *
     * @param instructorId The ID of the instructor.
     */
    private void updateInstructorRatingCoursesAndTags(String instructorId) {
        List<Review> reviews = reviewRepository.findAllByInstructorId(instructorId);
        double avgExperienceAndRating = reviews.stream().mapToDouble(r -> {
            if (r.getType().equals("course")) {
                return r.getExperience();
            } else {
                return r.getRating();
            }
        }).average().orElse(0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Set<Instructor.Tag> tags = reviews.stream().flatMap(r -> r.getTags().stream()).collect(Collectors.toSet());
        Set<Instructor.Course> courses = reviews.stream()
                .map(r -> courseMap.get(r.getCourseId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor != null) {
            int[][] distributions = getDistributions(reviews);
            instructor.setDifficultyDistribution(distributions[0]);
            instructor.setRatingDistribution(distributions[1]);
            instructor.setTags(tags);
            instructor.getCourses().addAll(courses);
            instructor.setAvgRating(avgExperienceAndRating);
            instructor.setAvgDifficulty(avgDifficulty);
            instructor.setReviewCount(reviewsCount);
            instructorRepository.save(instructor);
        }
    }

    private int[][] getDistributions(List<Review> reviews) {
        Map<Integer, Long> difficultyCount = reviews.stream()
                .collect(Collectors.groupingBy(Review::getDifficulty, Collectors.counting()));

        int[] difficultyDistribution = {
                Math.toIntExact(difficultyCount.getOrDefault(1, 0L)),
                Math.toIntExact(difficultyCount.getOrDefault(2, 0L)),
                Math.toIntExact(difficultyCount.getOrDefault(3, 0L)),
                Math.toIntExact(difficultyCount.getOrDefault(4, 0L)),
                Math.toIntExact(difficultyCount.getOrDefault(5, 0L))
        };

        Map<Integer, Long> experienceAndRatingCount = reviews.stream()
                .mapToInt(review -> {
                    if (review.getType().equals("course")) {
                        return review.getExperience();
                    } else {
                        return review.getRating();
                    }
                })
                .boxed()
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

        int[] experienceAndRatingDistribution = {
                Math.toIntExact(experienceAndRatingCount.getOrDefault(1, 0L)),
                Math.toIntExact(experienceAndRatingCount.getOrDefault(2, 0L)),
                Math.toIntExact(experienceAndRatingCount.getOrDefault(3, 0L)),
                Math.toIntExact(experienceAndRatingCount.getOrDefault(4, 0L)),
                Math.toIntExact(experienceAndRatingCount.getOrDefault(5, 0L))
        };

        return new int[][]{difficultyDistribution, experienceAndRatingDistribution};
    }

    /**
     * Checks if the user ID is blacklisted.
     *
     * @param userId The user ID to check.
     */
    private void checkBlacklistedUser(String userId) {
        if (blacklistService.isTokenBlacklisted(userId)) {
            throw CustomException("User is blacklisted and cannot add or update reviews");
        }
    }

    private RuntimeException exception(String... args) {
        return CustomExceptionFactory.throwCustomException(EntityType.REVIEW, ExceptionType.ENTITY_NOT_FOUND, args);
    }

    private RuntimeException CustomException(String... args) {
        return CustomExceptionFactory.throwCustomException(EntityType.REVIEW, ExceptionType.CUSTOM_EXCEPTION, args);
    }

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", allEntries = true),
            @CacheEvict(value = "instructorReviewsCache", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
    })
    @Transactional
    public ReviewDto deleteCommentFromReview(String reviewId, String commentId, String userId) {
        log.info("Deleting comment with ID: {} from review with ID: {}", commentId, reviewId);
        checkBlacklistedUser(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return exception(reviewId);
                });

        log.info("Review found with ID: {}", reviewId);

        // Validate and find the comment
        Comment comment = review.getComments()
                .stream()
                .filter(c -> c.get_id().equals(commentId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Comment not found with ID: {}", commentId);
                    return CustomExceptionFactory.throwCustomException(EntityType.COMMENT, ExceptionType.ENTITY_NOT_FOUND, commentId);
                });

        // Ensure the user owns the comment
        if (!comment.getUserId().equals(userId)) {
            log.error("User ID {} does not own the comment with ID: {}", userId, commentId);
            throw CustomExceptionFactory.throwCustomException(EntityType.COMMENT, ExceptionType.CUSTOM_EXCEPTION,
                    "User does not own this comment");
        }

        // Remove the comment and delete it
        review.getComments().remove(comment);
        commentRepository.deleteById(commentId);
        log.info("Comment with ID: {} deleted", commentId);

        // Save the updated review
        reviewRepository.save(review);
        log.info("Review with ID: {} updated after comment deletion", reviewId);

        return ReviewMapper.toDto(review);
    }

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", allEntries = true),
            @CacheEvict(value = "instructorReviewsCache", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
    })
    @Transactional
    public ReviewDto addCommentToReview(String reviewId, CommentDto commentDto) {
        log.info("Adding comment to review with ID: {}", reviewId);
        checkBlacklistedUser(commentDto.getUserId());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return exception(reviewId);
                });

        Comment comment = modelMapper.map(commentDto, Comment.class);
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment saved with ID: {}", savedComment.get_id());

        review.getComments().add(savedComment);
        reviewRepository.save(review);
        log.info("Comment added to review with ID: {}", reviewId);

        return ReviewMapper.toDto(review);
    }

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", allEntries = true),
            @CacheEvict(value = "instructorReviewsCache", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
    })
    @Transactional
    public ReviewDto updateCommentInReview(String reviewId, String commentId, CommentDto commentDto, String userId) {
        log.info("Updating comment with ID: {} in review with ID: {}", commentId, reviewId);
        checkBlacklistedUser(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return exception(reviewId);
                });

        log.info("Review found with ID: {}", reviewId);

        // Find the comment to update
        Comment commentToUpdate = review.getComments()
                .stream()
                .filter(c -> c.get_id().equals(commentId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Comment not found with ID: {}", commentId);
                    return CustomExceptionFactory.throwCustomException(EntityType.COMMENT, ExceptionType.ENTITY_NOT_FOUND, commentId);
                });

        // Ensure the user owns the comment
        if (!commentToUpdate.getUserId().equals(userId)) {
            log.error("User ID {} does not own the comment with ID: {}", userId, commentId);
            throw CustomExceptionFactory.throwCustomException(EntityType.COMMENT, ExceptionType.CUSTOM_EXCEPTION,
                    "User does not own this comment");
        }

        // Update the comment content
        commentToUpdate.setContent(commentDto.getContent());
        // Keep the original timestamp - only content should be updated
        
        // Save the updated comment
        commentRepository.save(commentToUpdate);
        log.info("Comment with ID: {} updated", commentId);

        // Save the review (to trigger any necessary updates)
        reviewRepository.save(review);
        log.info("Review with ID: {} updated after comment modification", reviewId);

        return ReviewMapper.toDto(review);
    }
}
