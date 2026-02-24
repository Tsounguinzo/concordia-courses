package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.model.instructor.InstructorReviewsDto;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.dto.response.ProcessingResult;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.service.InstructorService;
import courses.concordia.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    /**
     * Retrieves a list of instructors filtered according to the specified criteria, with pagination.
     * Results are cached to enhance performance for subsequent requests with the same parameters.
     *
     * @param limit The maximum number of instructors to return.
     * @param offset The offset from the start of the dataset for paging (e.g., for limit=10 and offset=20, start at the third page).
     * @param filters The filtering criteria to apply.
     * @return A list of {@link InstructorDto} objects that match the filter criteria, subject to the specified limit and offset.
     */
    @Cacheable(value = "instructorsCacheWithFilters", key = "{#limit, #offset, #filters.hashCode()}")
    @Override
    public List<InstructorDto> getInstructorsWithFilter(int limit, int offset, InstructorFilterDto filters) {
        log.info("Retrieving instructors with limit {}, offset {}, and filters {}", limit, offset, filters);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Instructor> page = findFilteredInstructors(filters, pageable);
        return page.getContent()
                .stream()
                .map(instructor -> modelMapper.map(instructor, InstructorDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an instructor by its unique identifier.
     * The result is cached to improve performance for subsequent requests with the same id.
     *
     * @param id The unique identifier of the instructor.
     * @return The {@link InstructorDto} representing the instructor.
     * @throws RuntimeException if the instructor with the specified id is not found.
     */
    @Cacheable(value = "instructorsCache", key = "#id")
    @Override
    public InstructorDto getInstructorById(String id) {
        log.info("Retrieving instructor with id: {}", id);
        Optional<Instructor> instructor = instructorRepository.findById(id);
        return instructor.map(i -> modelMapper.map(i, InstructorDto.class))
                .orElseThrow(() -> exception(id));
    }

    /**
     * Retrieves an instructor along with its reviews based on his id, with pagination.
     * The results are cached to improve performance on subsequent calls with the same id.
     *
     * @param id       The unique identifier for the instructor.
     * @param limit    The maximum number of reviews to return.
     * @param offset   The offset from the start of the dataset for paging.
     * @param userId  The unique identifier of the user requesting the reviews.
     * @param sortType The sorting criteria to apply to the reviews.
     * @return A {@link InstructorReviewsDto} object containing the instructor and its reviews.
     */
    @Cacheable(value = "instructorReviewsCache", key = "{#id, 'instructor', #limit, #offset, #sortType.hashCode()}")
    @Override
    public InstructorReviewsDto getInstructorAndReviewsByIdPaginated(String id, int limit, int offset, String userId, ReviewSortingDto sortType) {
        log.info("Retrieving instructor and reviews with id {} with pagination: limit={}, offset={}, sortType={}", id, limit, offset, sortType);
        InstructorDto instructor = getInstructorById(id);

        Criteria criteria = Criteria.where("instructorId").is(id);

        if (sortType.getSelectedCourse() != null && !sortType.getSelectedCourse().isBlank()) {
            criteria = criteria.and("courseId").is(sortType.getSelectedCourse());
        }

        Query query = new Query(criteria);

        if (sortType.getSortType() != null) {
            log.info("Applying sort by {} in {} order", sortType.getSortType(), sortType.isReverse() ? "DESC" : "ASC");
            switch (sortType.getSortType()) {
                case Difficulty:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "difficulty"));
                    break;
                case Rating, Experience:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "rating"));
                    break;
                case Likes:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "likes"));
                    break;
                case Recent:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "timestamp"));
                    break;
                default:
                    query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
                    break;
            }
        }

        long totalReviews = mongoTemplate.count(query, Review.class);

        query.with(PageRequest.of(offset / limit, limit));

        List<ReviewDto> reviews = mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());

        boolean hasUserReviewed = userId != null && !userId.equals("null") && mongoTemplate.exists(new Query(Criteria.where("userId").is(userId).and("instructorId").is(id)), Review.class);

        return new InstructorReviewsDto()
                .setInstructor(instructor)
                .setReviews(reviews)
                .setTotalReviews(totalReviews)
                .setHasUserReviewed(hasUserReviewed);
    }

    /**
     * Updates the statistics for all instructors in the repository.
     * This method calculates the average difficulty and rating ratings for each instructor based on reviews.
     */
    @Override
    public void updateInstructorsStatistics() {
        log.info("Updating Instructors statistics");

        List<Instructor> instructors = instructorRepository.findAll();
        List<Instructor> updatedInstructors = new ArrayList<>();

        for (Instructor instructor : instructors) {
            Query query = new Query(Criteria.where("instructorId").is(instructor.get_id()));
            List<Review> reviews = mongoTemplate.find(query, Review.class);

            double avgDifficulty = reviews.stream().mapToDouble(Review::getDifficulty).average().orElse(0.0);
            double avgExperienceAndRating = reviews.stream()
                    .mapToDouble(review -> "course".equals(review.getType()) ? review.getExperience() : review.getRating())
                    .average().orElse(0.0);

            Set<Instructor.Tag> tags = reviews.stream()
                    .map(Review::getTags)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

            Set<Instructor.Course> courses = reviews.stream()
                    .filter(review -> review.getCourseId() != null && !review.getCourseId().isBlank())
                    .map(review -> {
                        String courseId = review.getCourseId();
                        String schoolId = review.getSchoolId();

                        if (courseId.startsWith("CEWPMOD") || courseId.startsWith("CEBDMOD")
                                || courseId.startsWith("CEENMOD") || courseId.startsWith("CEBUMOD")) {
                            int splitIndex = courseId.indexOf("MOD");
                            return new Instructor.Course(courseId.substring(0, splitIndex), courseId.substring(splitIndex), schoolId);
                        }

                        Matcher matcher = Pattern.compile("(\\D*)(\\d.*)").matcher(courseId);
                        return matcher.matches()
                                ? new Instructor.Course(matcher.group(1), matcher.group(2), schoolId)
                                : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<Integer, Long> difficultyCount = reviews.stream()
                    .collect(Collectors.groupingBy(Review::getDifficulty, Collectors.counting()));

            int[] difficultyDistribution = new int[5];
            for (int i = 1; i <= 5; i++) {
                difficultyDistribution[i - 1] = Math.toIntExact(difficultyCount.getOrDefault(i, 0L));
            }

            Map<Integer, Long> ratingCount = reviews.stream()
                    .mapToInt(review -> "course".equals(review.getType()) ? review.getExperience() : review.getRating())
                    .boxed()
                    .collect(Collectors.groupingBy(i -> i, Collectors.counting()));

            int[] ratingDistribution = new int[5];
            for (int i = 1; i <= 5; i++) {
                ratingDistribution[i - 1] = Math.toIntExact(ratingCount.getOrDefault(i, 0L));
            }

            instructor.setDifficultyDistribution(difficultyDistribution);
            instructor.setRatingDistribution(ratingDistribution);
            instructor.setTags(tags);
            instructor.addCourses(courses);
            instructor.setAvgRating(avgExperienceAndRating);
            instructor.setAvgDifficulty(avgDifficulty);
            instructor.setReviewCount(reviews.size());

            updatedInstructors.add(instructor);
        }

        instructorRepository.saveAll(updatedInstructors);
        log.info("Instructors statistics updated for {} instructors", updatedInstructors.size());
    }

    @Override
    @CacheEvict(value = "homeStatsCache", allEntries = true)
    public ProcessingResult uploadInstructors(MultipartFile file) {
        log.info("Starting to upload instructors from file: {}", file.getOriginalFilename());
        List<InstructorDto> instructors = processInstructorFile(file);

        ProcessingResult result = new ProcessingResult();

        for (InstructorDto dto : instructors) {
            try {
                Instructor instructor = modelMapper.map(dto, Instructor.class);
                Instructor existingInstructor = instructorRepository.findById(instructor.get_id()).orElse(null);

                if (existingInstructor != null) {
                    result.incrementAlreadyExists();
                    // Update existing instructor
                    if (instructor.getCourses() != null && !existingInstructor.getCourses().containsAll(instructor.getCourses())) {
                        existingInstructor.addCourses(instructor.getCourses());
                        instructorRepository.save(existingInstructor);
                        result.incrementUpdated();
                    }
                } else {
                    // New Instructor, add it
                    instructorRepository.save(instructor);
                    result.incrementAdded();
                }

            } catch (Exception e) {
                String errorMsg = String.format("Failed to process instructor (instructorId=%s): %s",
                        dto.get_id(), e.getMessage());
                log.error(errorMsg, e);
                result.addError(errorMsg);
                result.incrementFailed();
            }
        }

        log.info("Instructors processing completed. Added: {}, AlreadyExist: {}, Failed: {}",
                result.getAddedCount(), result.getAlreadyExistsCount(), result.getFailedCount());

        if (!result.getErrors().isEmpty()) {
            log.info("There were errors during processing: {}", result.getErrors());
        }

        return result;
    }

    private List<InstructorDto> processInstructorFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return JsonUtils.getData(inputStream, new TypeToken<List<InstructorDto>>() {});
        } catch (IOException e) {
            String errorMsg = "Failed to process professor file: " + file.getOriginalFilename();
            log.error(errorMsg, e);
            throw new RuntimeException("Failed to process instructor file", e);
        }
    }

    /**
     * Finds and returns a page of instructors filtered by the given criteria.
     *
     * @param filter The filtering criteria applied to instructor search.
     * @param pageable Pagination information.
     * @return A page of instructors that match the filtering criteria.
     */
    private Page<Instructor> findFilteredInstructors(InstructorFilterDto filter, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        // Handle 'levels' filter
        buildLevelCriteria(filter, criteriaList);

        // Handle 'subjects' filter
        buildSubjectCriteria(filter, criteriaList);

        // Handle 'departments' filter
        buildDepartmentCriteria(filter, criteriaList);

        // Handle 'tags' filter
        buildTagsCriteria(filter, criteriaList);

        // Handle 'query' filter ('query' can be matched against multiple fields like title or description)
        buildQueryCriteria(filter, criteriaList);


        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Handle sorting
        applySorting(filter, query);

        List<Instructor> instructors = mongoTemplate.find(query, Instructor.class);
        long count = instructors.size();

        log.info("Found {} instructors matching filter criteria", count);

        return new PageImpl<>(instructors, pageable, count);
    }

    /**
     * Builds the criteria for filtering instructors by course levels.
     *
     * @param filter The instructor filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildLevelCriteria(InstructorFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getLevels() != null && !filter.getLevels().isEmpty()) {
            String regexPattern = "^(" + filter.getLevels().stream()
                    .map(lvl -> String.format("%s\\d{%d}(?![\\d])", lvl.charAt(0), lvl.length() - 1))
                    .collect(Collectors.joining("|")) + ")";
            criteriaList.add(Criteria.where("courses").elemMatch(Criteria.where("catalog").regex(regexPattern)));
            log.info("Filtering by course levels with: {} with Regex {}", filter.getLevels(), regexPattern);
        }
    }

    /**
     * Builds the criteria for filtering instructors by courses subject.
     *
     * @param filter The instructor filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildSubjectCriteria(InstructorFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getSubjects() != null && !filter.getSubjects().isEmpty()) {
            String regexPattern = "^(" + String.join("|", filter.getSubjects()) + ")";
            criteriaList.add(Criteria.where("courses").elemMatch(Criteria.where("subject").regex(regexPattern)));
            log.info("Filtering by courses subject: {}", filter.getSubjects());
        }
    }

    /**
     * Builds the criteria for filtering instructors by department.
     *
     * @param filter The instructor filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildDepartmentCriteria(InstructorFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getDepartments() != null && !filter.getDepartments().isEmpty()) {
            criteriaList.add(Criteria.where("departments").in(filter.getDepartments()));
            log.info("Filtering by departments to include all specified: {}", filter.getDepartments());
        }
    }

    /**
     * Builds the criteria for filtering instructors by tags.
     *
     * @param filter The instructor filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildTagsCriteria(InstructorFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            criteriaList.add(Criteria.where("tags").in(filter.getTags()));
            log.info("Filtering by tags to include all specified: {}", filter.getTags());
        }
    }

    /**
     * Builds the criteria for filtering instructors based on a general query.
     * The query can match multiple fields like name, department, etc.
     *
     * @param filter The instructor filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildQueryCriteria(InstructorFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getQuery() != null && !filter.getQuery().isEmpty()) {
            String idRegexPattern = ".*" + filter.getQuery().replaceAll("\\s+", "-") + ".*";
            String restRegexPattern = ".*" + filter.getQuery() + ".*";
            Criteria queryCriteria = new Criteria().orOperator(
                    Criteria.where("_id").regex(idRegexPattern, "i"),
                    Criteria.where("firstName").regex(restRegexPattern, "i"),
                    Criteria.where("lastName").regex(restRegexPattern, "i"),
                    Criteria.where("department").regex(restRegexPattern, "i"),
                    Criteria.where("tags").regex(restRegexPattern, "i"),
                    Criteria.where("courses").elemMatch(Criteria.where("catalog").regex(restRegexPattern, "i")),
                    Criteria.where("courses").elemMatch(Criteria.where("subject").regex(restRegexPattern, "i"))
            );
            criteriaList.add(queryCriteria);
            log.info("Applying query filter: {}", filter.getQuery());
        }
    }

    /**
     * Applies sorting to the query based on the sort criteria specified in the filter.
     *
     * @param filter The course filter DTO containing sorting criteria.
     * @param query The MongoDB query to which sorting will be applied.
     */
    private void applySorting(InstructorFilterDto filter, Query query) {
        if (filter.getSortBy() != null) {
            InstructorFilterDto.CourseSort sort = filter.getSortBy();
            log.info("Applying sort by {} in {} order", sort.getSortType(), sort.isReverse() ? "DESC" : "ASC");
            switch (sort.getSortType()) {
                case Difficulty:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "avgDifficulty"));
                    break;
                case Rating:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "avgRating"));
                    break;
                case ReviewCount:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "reviewCount"));
                    break;
                default:
                    query.with(Sort.by(Sort.Direction.ASC, "firstName", "lastName"));
                    break;
            }
        }
    }

    private RuntimeException exception(String... args) {
        return CustomExceptionFactory.throwCustomException(EntityType.INSTRUCTOR, ExceptionType.ENTITY_NOT_FOUND, args);
    }
}
