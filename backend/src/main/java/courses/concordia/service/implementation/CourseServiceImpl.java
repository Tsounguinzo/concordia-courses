package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.CourseFilterDto;
import courses.concordia.dto.model.course.CourseReviewsDto;
import courses.concordia.dto.model.instructor.CourseInstructorDto;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Course;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.service.CourseService;
import courses.concordia.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    /**
     * Retrieves a list of courses filtered according to the specified criteria, with pagination.
     * Results are cached to enhance performance for subsequent requests with the same parameters.
     *
     * @param limit The maximum number of courses to return.
     * @param offset The offset from the start of the dataset for paging (e.g., for limit=10 and offset=20, start at the third page).
     * @param filters The filtering criteria to apply.
     * @return A list of {@link CourseDto} objects that match the filter criteria, subject to the specified limit and offset.
     */
    @Cacheable(value = "coursesCacheWithFilters", key = "{#limit, #offset, #filters.hashCode()}")
    @Override
    public List<CourseDto> getCoursesWithFilter(int limit, int offset, CourseFilterDto filters) {
        log.info("Retrieving courses with limit {}, offset {}, and filters {}", limit, offset, filters);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Course> page = findFilteredCourses(filters, pageable);
        return page.getContent()
                .stream()
                .map(course -> modelMapper.map(course, CourseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a course by its unique identifier.
     * The result is cached to improve performance for subsequent requests with the same ID.
     *
     * @param id The unique identifier of the course.
     * @return The {@link CourseDto} representing the course.
     * @throws RuntimeException if the course with the specified ID is not found.
     */
    @Cacheable(value = "coursesCache", key = "#id")
    @Override
    public CourseDto getCourseById(String id) {
        log.info("Retrieving course with ID {}", id);
        Optional<Course> course = courseRepository.findById(id);
        return course.map(c -> modelMapper.map(c, CourseDto.class))
                .orElseThrow(() -> exception(id));
    }

    /**
     * Retrieves a course along with its reviews based on the course ID, with pagination.
     * The results are cached to improve performance on subsequent calls with the same ID.
     *
     * @param id       The unique identifier for the course.
     * @param limit    The maximum number of reviews to return.
     * @param offset   The offset from the start of the dataset for paging.
     * @param userId  The unique identifier of the user requesting the reviews.
     * @param sortType The sorting criteria to apply to the reviews.
     * @return A {@link CourseReviewsDto} object containing the course and its reviews.
     */
    @Cacheable(value = "courseReviewsCache", key = "{#id, 'course', #limit, #offset, #sortType.hashCode()}")
    @Override
    public CourseReviewsDto getCourseAndReviewsByIdPaginated(String id, int limit, int offset, String userId, ReviewSortingDto sortType) {
        log.info("Retrieving course and reviews with ID {} with limit {}, offset {}, and sorting {}", id, limit, offset, sortType);
        CourseDto course = getCourseById(id);

        Criteria criteria = Criteria.where("courseId").is(id);

        if (sortType.getSelectedInstructor() != null && !sortType.getSelectedInstructor().isBlank()) {
            criteria = criteria.and("instructorId").is(sortType.getSelectedInstructor());
        }

        Query query = new Query(criteria);

        if (sortType.getSortType() != null) {
            log.info("Applying sort by {} in {} order", sortType.getSortType(), sortType.isReverse() ? "DESC" : "ASC");
            switch (sortType.getSortType()) {
                case Difficulty:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "difficulty"));
                    break;
                case Rating, Experience:
                    query.with(Sort.by(sortType.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "experience"));
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

        boolean hasUserReviewed = userId != null && !userId.equals("null") && mongoTemplate.exists(new Query(Criteria.where("userId").is(userId).and("courseId").is(id)), Review.class);

        return new CourseReviewsDto()
                .setCourse(course)
                .setReviews(reviews)
                .setTotalReviews(totalReviews)
                .setHasUserReviewed(hasUserReviewed);

    }

    /**
     * Updates the courses in the repository based on the contents of the provided file.
     * This method is used to update the course schedules from an external source.
     *
     * @param file The file containing the updated course information.
     */
    @Override
    public void updateCourses(MultipartFile file) {
        log.info("Updating courses schedules from file: {}", file.getOriginalFilename());
        List<Course> courses = processCourseFile(file);
        courses.forEach(this::updateSchedules);
        log.info("{} courses updated successfully", courses.size());
    }

    /**
     * Updates the statistics for all courses in the repository.
     * This method calculates the average difficulty and experience ratings for each course based on reviews.
     */
    @Override
    public void updateCoursesStatistics() {
        log.info("Updating courses statistics");
        List<Course> courses = courseRepository.findAll();
        courses.forEach(course -> {
            Query query = new Query(Criteria.where("courseId").is(course.get_id()));
            List<Review> reviews = mongoTemplate.find(query, Review.class);
            double avgDifficulty = reviews.stream().mapToDouble(Review::getDifficulty).average().orElse(0);
            double avgExperienceAndRating = reviews.stream().mapToDouble(review -> {
                if (review.getType().equals("course")) {
                    return review.getExperience();
                } else {
                    return review.getRating();
                }
            }).average().orElse(0);
            course.setAvgDifficulty(avgDifficulty);
            course.setAvgExperience(avgExperienceAndRating);
            course.setReviewCount(reviews.size());
            courseRepository.save(course);
        });
        log.info("Courses statistics updated successfully");
    }

    @Cacheable(value = "courseInstructorsCache", key = "#id")
    @Override
    public List<CourseInstructorDto> getInstructors(String id) {
        log.info("Retrieving instructors for course with ID {}", id);
        CourseDto course = getCourseById(id);
        return instructorRepository.findByCoursesContaining(Set.of(new Instructor.Course(course.getSubject(), course.getCatalog())))
                .stream()
                .map(instructor -> modelMapper.map(instructor, CourseInstructorDto.class))
                .collect(Collectors.toList());
    }

    private void updateSchedules(Course course) {
        courseRepository.findById(course.get_id())
                .ifPresentOrElse(existingCourse -> {
                    existingCourse.setSchedules(course.getSchedules());
                    existingCourse.setTerms(course.getTerms());
                    existingCourse.setClassUnit(course.getClassUnit());
                    existingCourse.setConUCourseID(course.getConUCourseID());
                    courseRepository.save(existingCourse);
                }, () -> courseRepository.save(course));
    }

    private List<Course> processCourseFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return JsonUtils.getData(inputStream, new TypeToken<List<Course>>() {});
        } catch (IOException e) {
            log.error("Failed to process course file", e);
            throw new RuntimeException("Failed to process course file", e);
        }
    }

    /**
     * Finds and returns a page of courses filtered by the given criteria.
     *
     * @param filter The filtering criteria applied to course search.
     * @param pageable Pagination information.
     * @return A page of courses that match the filtering criteria.
     */
    private Page<Course> findFilteredCourses(CourseFilterDto filter, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        // Handle 'levels' filter
        buildLevelCriteria(filter, criteriaList);

        // Handle 'subjects' filter
        buildSubjectCriteria(filter, criteriaList);

        // Handle 'terms' filter
        buildTermCriteria(filter, criteriaList);

        // Handle 'query' filter ('query' can be matched against multiple fields like title or description)
        buildQueryCriteria(filter, criteriaList);


        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Handle sorting
        applySorting(filter, query);

        long count = mongoTemplate.count(query, Course.class);
        List<Course> courses = mongoTemplate.find(query, Course.class);

        log.info("Found {} courses matching filter criteria", courses.size());

        return prioritizeCoursesByIdMatch(courses, filter.getQuery(), pageable, count);
    }

    /**
     * Builds the criteria for filtering courses by levels.
     *
     * @param filter The course filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildLevelCriteria(CourseFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getLevels() != null && !filter.getLevels().isEmpty()) {
            String regexPattern = "^(" + filter.getLevels().stream()
                    .map(lvl -> String.format("%s\\d{%d}(?![\\d])", lvl.charAt(0), lvl.length() - 1))
                    .collect(Collectors.joining("|")) + ")";
            criteriaList.add(Criteria.where("catalog").regex(regexPattern));
            log.info("Filtering by levels with: {} with Regex {}", filter.getLevels(), regexPattern);
        }
    }

    /**
     * Builds the criteria for filtering courses by subjects.
     *
     * @param filter The course filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildSubjectCriteria(CourseFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getSubjects() != null && !filter.getSubjects().isEmpty()) {
            String regexPattern = "^(" + String.join("|", filter.getSubjects()) + ")";
            criteriaList.add(Criteria.where("subject").regex(regexPattern));
            log.info("Filtering by subjects: {}", filter.getSubjects());
        }
    }

    /**
     * Builds the criteria for filtering courses by terms.
     *
     * @param filter The course filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildTermCriteria(CourseFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getTerms() != null && !filter.getTerms().isEmpty()) {
            criteriaList.add(Criteria.where("terms").all(filter.getTerms()));
            log.info("Filtering by terms to include all specified: {}", filter.getTerms());
        }
    }

    /**
     * Builds the criteria for filtering courses based on a general query.
     * The query can match multiple fields like title, description, etc.
     *
     * @param filter The course filter DTO containing filter criteria.
     * @param criteriaList The list of criteria to which the new criteria will be added.
     */
    private void buildQueryCriteria(CourseFilterDto filter, List<Criteria> criteriaList) {
        if (filter.getQuery() != null && !filter.getQuery().isEmpty()) {
            String idRegexPattern = ".*" + filter.getQuery().replaceAll("\\s+", "") + ".*";
            String restRegexPattern = ".*" + filter.getQuery() + ".*";
            Criteria queryCriteria = new Criteria().orOperator(
                    Criteria.where("_id").regex(idRegexPattern, "i"),
                    Criteria.where("catalog").regex(restRegexPattern, "i"),
                    Criteria.where("title").regex(restRegexPattern, "i"),
                    Criteria.where("subject").regex(restRegexPattern, "i"),
                    Criteria.where("description").regex(restRegexPattern, "i")
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
    private void applySorting(CourseFilterDto filter, Query query) {
        if (filter.getSortBy() != null) {
            CourseFilterDto.CourseSort sort = filter.getSortBy();
            log.info("Applying sort by {} in {} order", sort.getSortType(), sort.isReverse() ? "DESC" : "ASC");
            switch (sort.getSortType()) {
                case Difficulty:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "avgDifficulty"));
                    break;
                case Experience:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "avgExperience"));
                    break;
                case ReviewCount:
                    query.with(Sort.by(sort.isReverse() ? Sort.Direction.DESC : Sort.Direction.ASC, "reviewCount"));
                    break;
                default:
                    query.with(Sort.by(Sort.Direction.ASC, "_id"));
                    break;
            }
        }
    }

    /**
     * Prioritizes courses based on a match of their _id field against a given query string.
     * This method assumes it's operating on a subset (a page) of the total results and uses the total count of matches for pagination.
     *
     * @param courses The list of courses (a page of results) to prioritize.
     * @param query The query string used for matching courses' _id.
     * @param pageable Pageable object containing pagination information.
     * @param totalCount The total count of courses that match the filter criteria.
     * @return A page of courses prioritized by _id matches.
     */
    private Page<Course> prioritizeCoursesByIdMatch(List<Course> courses, String query, Pageable pageable, long totalCount) {
        if (query != null && !query.isBlank()){
            final Pattern idPattern = Pattern.compile(".*" + Pattern.quote(query.replaceAll("\\s+", "")) + ".*", Pattern.CASE_INSENSITIVE);

            // Sort courses with a custom comparator that prioritizes _id matches
            List<Course> prioritizedCourses = courses.stream()
                    .sorted((course1, course2) -> {
                        boolean idMatch1 = idPattern.matcher(course1.get_id()).matches();
                        boolean idMatch2 = idPattern.matcher(course2.get_id()).matches();
                        return Boolean.compare(idMatch2, idMatch1); // Prioritize true (match) over false
                    })
                    .collect(Collectors.toList());

            // Return the sorted list within a new PageImpl to maintain pagination,
            // using the original totalCount for accurate total pages calculation
            return new PageImpl<>(prioritizedCourses, pageable, totalCount);
        }

        // If no query is provided or prioritization is not needed,
        // return the original list and total count without modification
        return new PageImpl<>(courses, pageable, totalCount);
    }


    private RuntimeException exception(String... args) {
        return CustomExceptionFactory.throwCustomException(EntityType.COURSE, ExceptionType.ENTITY_NOT_FOUND, args);
    }
}
