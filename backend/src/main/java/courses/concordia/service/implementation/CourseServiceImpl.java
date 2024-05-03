package courses.concordia.service.implementation;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.CourseReviewsDto;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Course;
import courses.concordia.dto.model.course.CourseFilterDto;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    /**
     * Retrieves all available courses from the repository.
     * The results are cached to improve performance for subsequent requests.
     *
     * @return A list of {@link CourseDto} representing all courses.
     */
    @Cacheable(value = "coursesCache")
    @Override
    public List<CourseDto> getCourses() {
        log.info("Retrieving all courses");
        return courseRepository.findAll()
                .stream()
                .map(course -> modelMapper.map(course, CourseDto.class))
                .collect(Collectors.toList());
    }

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
     * Retrieves a course along with its reviews based on the course ID.
     * The results are cached to improve performance on subsequent calls with the same ID.
     *
     * @param id The unique identifier for the course.
     * @return A {@link CourseReviewsDto} object containing the course and its reviews.
     */
    @Cacheable(value = "courseReviewsCache", key = "{#id, 'course'}")
    @Override
    public CourseReviewsDto getCourseAndReviewsById(String id) {
        log.info("Retrieving course and reviews with ID {}", id);
        CourseDto course = getCourseById(id);

        Query query = new Query(Criteria.where("courseId").is(id)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        List<ReviewDto> reviews = mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());

        return new CourseReviewsDto()
                .setCourse(course)
                .setReviews(reviews);
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
