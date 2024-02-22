package courses.concordia.service;

import courses.concordia.dto.CourseReviewsDTO;
import courses.concordia.exception.CourseNotFoundException;
import courses.concordia.model.Course;
import courses.concordia.model.CourseFilter;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CourseServiceImpl implements CourseService{
    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    @Override
    public List<Course> getCourses() {
        log.info("Retrieving all courses");
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCourses(int limit, int offset, CourseFilter filters) {
        log.info("Retrieving courses with limit {}, offset {}, and filters {}", limit, offset, filters);
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Course> page = findFilteredCourses(filters, pageable);
        return page.getContent();
    }

    @Override
    public Course getCourseById(String id) {
        log.info("Retrieving course with ID {}", id);
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
    }

    @Override
    public CourseReviewsDTO getCourseAndReviewsById(String id) {
        log.info("Retrieving course and reviews with ID {}", id);
        Course course = getCourseById(id);

        Query query = new Query(Criteria.where("courseId").is(id));
        List<Review> reviews = mongoTemplate.find(query, Review.class);

        return new CourseReviewsDTO(course, reviews);
    }

    private Page<Course> findFilteredCourses(CourseFilter filter, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        log.info("Building query for courses with filters");

        // Handle 'levels' filter
        if (filter.getLevels() != null && !filter.getLevels().isEmpty()) {
            String regexPattern = "^(" + String.join("|", filter.getLevels()) + ")";
            criteriaList.add(Criteria.where("catalog").regex(regexPattern));
            log.info("Filtering by levels starting with: {}", filter.getLevels());
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
        if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
            for (CourseFilter.CourseSort sort : filter.getSortBy()) {
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
                        query.with(Sort.by(Sort.Direction.ASC, "_id"));
                        break;
                }
            }
        } else {
            query.with(Sort.by(Sort.Direction.ASC, "_id"));
        }

        long count = mongoTemplate.count(query, Course.class);
        List<Course> courses = mongoTemplate.find(query, Course.class);

        log.info("Found {} courses matching filter criteria", courses.size());

        return new PageImpl<>(courses, pageable, count);
    }
}
