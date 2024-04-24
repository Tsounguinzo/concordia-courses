package courses.concordia.service.implementation;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.model.instructor.InstructorReviewsDto;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.service.InstructorService;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    /**
     * Retrieves all available instructors from the repository.
     * The results are cached to improve performance for subsequent requests.
     *
     * @return A list of {@link InstructorDto} representing all courses.
     */
    @Cacheable(value = "instructorsCache")
    @Override
    public List<InstructorDto> getInstructors() {
        log.info("Retrieving all instructors");
        return instructorRepository.findAll()
                .stream()
                .map(instructor -> modelMapper.map(instructor, InstructorDto.class))
                .toList();
    }

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
     * The result is cached to improve performance for subsequent requests with the same name.
     *
     * @param name The unique identifier of the instructor.
     * @return The {@link InstructorDto} representing the instructor.
     * @throws RuntimeException if the instructor with the specified name is not found.
     */
    @Cacheable(value = "instructorsCache", key = "#name")
    @Override
    public InstructorDto getInstructorByName(String name) {
        log.info("Retrieving instructor named {}", name);
        Optional<Instructor> instructor = instructorRepository.findById(name);
        return instructor.map(i -> modelMapper.map(i, InstructorDto.class))
                .orElseThrow(() -> exception(name));
    }

    /**
     * Retrieves an instructor along with its reviews based on his name.
     * The results are cached to improve performance on subsequent calls with the same name.
     *
     * @param name The unique identifier for the instructor.
     * @return A {@link InstructorReviewsDto} object containing the instructor and its reviews.
     */
    @Cacheable(value = "instructorReviewsCache", key = "#name")
    @Override
    public InstructorReviewsDto getInstructorAndReviewsByName(String name) {
        log.info("Retrieving instructor and reviews with name {}", name);
        InstructorDto instructor = getInstructorByName(name);

        Query query = new Query(Criteria.where("instructor").is(name)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        List<ReviewDto> reviews = mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());

        return new InstructorReviewsDto()
                .setInstructor(instructor)
                .setReviews(reviews);
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

        // Handle 'query' filter ('query' can be matched against multiple fields like title or description)
        buildQueryCriteria(filter, criteriaList);


        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long count = mongoTemplate.count(query, Instructor.class);
        List<Instructor> instructors = mongoTemplate.find(query, Instructor.class);

        log.info("Found {} instructors matching filter criteria", instructors.size());

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
            criteriaList.add(Criteria.where("department").in(filter.getDepartments()));
            log.info("Filtering by departments to include all specified: {}", filter.getDepartments());
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
                    Criteria.where("courses").elemMatch(Criteria.where("catalog").regex(restRegexPattern, "i")),
                    Criteria.where("courses").elemMatch(Criteria.where("subject").regex(restRegexPattern, "i"))
            );
            criteriaList.add(queryCriteria);
            log.info("Applying query filter: {}", filter.getQuery());
        }
    }

    private RuntimeException exception(String... args) {
        return CustomExceptionFactory.throwCustomException(EntityType.INSTRUCTOR, ExceptionType.ENTITY_NOT_FOUND, args);
    }
}