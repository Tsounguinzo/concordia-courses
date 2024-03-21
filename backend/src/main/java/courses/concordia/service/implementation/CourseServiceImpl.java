package courses.concordia.service.implementation;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.CourseReviewsDto;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.exception.CCException;
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
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.COURSE;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    @Override
    public List<CourseDto> getCourses() {
        log.info("Retrieving all courses");
        return courseRepository.findAll()
                .stream()
                .map(course -> modelMapper.map(course, CourseDto.class))
                .collect(Collectors.toList());
    }

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

    @Override
    public CourseDto getCourseById(String id) {
        log.info("Retrieving course with ID {}", id);
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            return modelMapper.map(course.get(), CourseDto.class);
        }
        throw exception(COURSE, ENTITY_NOT_FOUND, id);
    }

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

    private Page<Course> findFilteredCourses(CourseFilterDto filter, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        log.info("Building query for courses with filters");

        // Handle 'levels' filter
        if (filter.getLevels() != null && !filter.getLevels().isEmpty()) {
            List<String> levelsRegex = filter.getLevels().stream()
                    .map(lvl -> String.format("%s\\d{%d}(?![\\d])", lvl.charAt(0), lvl.length() - 1))
                    .toList();
            String regexPattern = "^(" + String.join("|", levelsRegex) + ")";
            criteriaList.add(Criteria.where("catalog").regex(regexPattern));
            log.info("Filtering by levels with: {} with Regex {}", filter.getLevels(), levelsRegex);
        }

        // Handle 'subjects' filter
        if (filter.getSubjects() != null && !filter.getSubjects().isEmpty()) {
            String regexPattern = "^(" + String.join("|", filter.getSubjects()) + ")";
            criteriaList.add(Criteria.where("subject").regex(regexPattern));
            log.info("Filtering by subjects: {}", filter.getSubjects());
        }

        // Handle 'terms' filter
        if (filter.getTerms() != null && !filter.getTerms().isEmpty()) {
            criteriaList.add(Criteria.where("terms").all(filter.getTerms()));
            log.info("Filtering by terms to include all specified: {}", filter.getTerms());
        }

        // Handle 'query' filter ('query' can be matched against multiple fields like title or description)
        if (filter.getQuery() != null && !filter.getQuery().isEmpty()) {
            String idRegexPattern = ".*" + filter.getQuery().replace(" ", "") + ".*";
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

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Handle sorting
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

        long count = mongoTemplate.count(query, Course.class);
        List<Course> courses = mongoTemplate.find(query, Course.class);

        log.info("Found {} courses matching filter criteria", courses.size());

        return new PageImpl<>(courses, pageable, count);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
