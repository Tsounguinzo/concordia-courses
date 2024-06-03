package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Course;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.repository.ReviewRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static courses.concordia.util.DateUtils.getLocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
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
            Map<String, List<String>> coursesData = JsonUtils.getData(is, new TypeToken<Map<String, List<String>>>() {});
            if (coursesData != null) {
                coursesData.forEach((key, values) -> values.forEach(value -> {
                    String courseKey = key + value;
                    Instructor.Course course = new Instructor.Course(key, value);
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
            @CacheEvict(value = "courseReviewsCache", key = "{#reviewDto.courseId, #reviewDto.type}", condition = "#reviewDto.type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", key = "{#reviewDto.instructorId, #reviewDto.type}", condition = "#reviewDto.type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true, condition = "#reviewDto.type.equals('course')"),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true, condition = "#reviewDto.type.equals('instructor')"),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCache", allEntries = true),
    })
    @Transactional
    @Override
    public ReviewDto addOrUpdateReview(ReviewDto reviewDto) {
        validateTimestamp(reviewDto.getTimestamp());
        checkBlacklistedUser(reviewDto.getUserId());

        Review review;
        if (reviewDto.getType() != null) {
            if (reviewDto.getType().equals("instructor")) {
                review = reviewRepository
                        .findByInstructorIdAndUserIdAndType(reviewDto.getInstructorId(), reviewDto.getUserId(), reviewDto.getType())
                        .map(r -> updateReviewFromDto(r, reviewDto))
                        .orElseGet(() -> createReviewFromDto(reviewDto));
                updateInstructorRatingCoursesAndTags(review);
            } else {
                review = reviewRepository
                        .findByCourseIdAndUserIdAndType(reviewDto.getCourseId(), reviewDto.getUserId(), reviewDto.getType())
                        .map(r -> updateReviewFromDto(r, reviewDto))
                        .orElseGet(() -> createReviewFromDto(reviewDto));
                updateCourseExperience(review.getCourseId());
            }

            review = reviewRepository.save(review);

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
        return reviewRepository.save(modelMapper.map(reviewDto, Review.class));
    }

    /**
     * Updates an existing Review entity from a ReviewDto.
     *
     * @param existingReview The existing Review entity.
     * @param reviewDto The review data transfer object with updated fields.
     * @return The updated Review entity.
     */
    private Review updateReviewFromDto(Review existingReview, ReviewDto reviewDto) {
        modelMapper.map(reviewDto, existingReview);
        if (reviewDto.getTags().isEmpty()) existingReview.setTags(Collections.emptySet()); // By default, ModelMapper does not map empty collections
        return reviewRepository.save(existingReview);
    }

    /**
     * Deletes a review identified by courseId and userId.
     *
     * @param id The ID of the review.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "{#courseId, 'course'}", condition = "#type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", key = "{#instructorId, 'instructor'}", condition = "#type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true),
            @CacheEvict(value = "instructorsCache", allEntries = true)
    })
    @Transactional
    @Override
    public void deleteReview(String id, String type, String courseId, String instructorId) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isEmpty() || review.get().getType() == null) {
            throw exception(id);
        } else {
            if (review.get().getType().equals("instructor")){
                reviewRepository.deleteById(id);
                updateInstructorRatingCoursesAndTags(review.get());
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
     * @param limit The maximum number of reviews to return.
     * @param offset The offset from where to start the query.
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
     */
    @Override
    public void uploadReviews(MultipartFile file) {
        log.info("Uploading reviews from file: {}", file.getOriginalFilename());
        List<ReviewDto> reviewDtos = processReviewFile(file);
        reviewDtos.forEach(this::addOrUpdateReview);
        log.info("{} reviews processed successfully", reviewDtos.size());
    }

    private List<ReviewDto> processReviewFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return JsonUtils.getData(inputStream, new TypeToken<List<ReviewDto>>() {});
        } catch (IOException e) {
            log.error("Failed to process review file", e);
            throw new RuntimeException("Failed to process review file", e);
        }
    }

    /**
     * Finds reviews based on filtering criteria with pagination support.
     *
     * @param filters The filtering criteria for reviews.
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
     * @param filter The filter criteria for reviews.
     * @param query The query to which sorting will be applied.
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
        List<Review> reviews = reviewRepository.findAllByCourseIdAndType(courseId, "course");
        double avgExperience = reviews.stream().mapToInt(Review::getExperience).average().orElse(0.0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setAvgExperience(avgExperience);
            course.setAvgDifficulty(avgDifficulty);
            course.setReviewCount(reviewsCount);
            courseRepository.save(course);
        }
    }

    /**
     * Updates the average rating, difficulty and tags for a prof based on its reviews.
     *
     * @param review The review object.
     */
    private void updateInstructorRatingCoursesAndTags(Review review){
        String instructorId = review.getInstructorId();
        List<Review> reviews = reviewRepository.findAllByInstructorIdAndType(instructorId, "instructor");
        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Set<Instructor.Tag> tags = reviews.stream().flatMap(r -> r.getTags().stream()).collect(Collectors.toSet());
        Set<Instructor.Course> courses = reviews.stream()
                .map(r -> courseMap.get(r.getCourseId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor != null) {
            instructor.setTags(tags);
            instructor.getCourses().addAll(courses);
            instructor.setAvgRating(avgRating);
            instructor.setAvgDifficulty(avgDifficulty);
            instructor.setReviewCount(reviewsCount);
            instructorRepository.save(instructor);
        }
    }
    /**
     * Validates the timestamp to ensure it is not in the future or too far in the past.
     *
     * @param timestamp The timestamp to validate.
     */
    private void validateTimestamp(LocalDateTime timestamp) {
        LocalDateTime now = getLocalDateTime();
        if (timestamp.isAfter(now)) {
            throw CustomException("Timestamp cannot be in the future");
        }
        if (timestamp.isBefore(now.minusSeconds(30))) {
            throw CustomException("Timestamp cannot be more than 30 seconds in the past");
        }
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
}
