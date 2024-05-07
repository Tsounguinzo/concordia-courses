package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.InteractionMapper;
import courses.concordia.dto.model.interaction.InteractionDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Interaction;
import courses.concordia.model.Review;
import courses.concordia.repository.InteractionRepository;
import courses.concordia.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class InteractionServiceImpl implements InteractionService {
    private final InteractionRepository interactionRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Fetches all interactions a user has for a specific course or instructor.
     *
     * @param id The ID of the course or instructor.
     * @param referrer The referrer identifier.
     * @param type The type of the interaction.
     * @return A list of {@link InteractionDto} objects.
     */
    @Override
    public List<InteractionDto> getUserInteractions(String id, String referrer, String type) {
        if (type.equals("course")) {
            return getUserInteractionsForCourse(id, referrer, type);
        } else if (type.equals("instructor")) {
            return getUserInteractionsForInstructor(id, referrer, type);
        }
        return Collections.emptyList();
    }

    /**
     * Fetches all interactions a user has for a specific course and referrer.
     *
     * @param courseId The ID of the course.
     * @param referrer The referrer identifier.
     * @param type The type of the interaction.
     * @return A list of {@link InteractionDto} objects.
     */
    private List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer, String type) {
        return interactionRepository.findByCourseIdAndReferrerAndType(courseId, referrer, type).stream()
                .map(InteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Fetches all interactions a user has for a specific instructor and referrer.
     *
     * @param instructorId       The ID of the instructor.
     * @param referrer The referrer identifier.
     * @param type The type of the interaction.
     * @return A list of {@link InteractionDto} objects.
     */
    private List<InteractionDto> getUserInteractionsForInstructor(String instructorId, String referrer, String type) {
        return interactionRepository.findByInstructorIdAndReferrerAndType(instructorId, referrer, type).stream()
                .map(InteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Adjusts the likes count for a review based on user interaction changes.
     *
     * @param courseId    The ID of the course.
     * @param userId      The ID of the user.
     * @param likesChange The amount to adjust the likes by.
     */
    private void adjustReviewLikes(String courseId, String instructorId, String userId, int likesChange, String ReviewType) {
        Query reviewQuery = new Query();
        if (ReviewType.equals("instructor")) {
            reviewQuery = new Query(Criteria.where("instructorId").is(instructorId).and("userId").is(userId).and("type").is("instructor"));
        } else if (ReviewType.equals("course")) {
            reviewQuery = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId).and("type").is("course"));
        }
        Update update = new Update().inc("likes", likesChange);
        mongoTemplate.updateFirst(reviewQuery, update, Review.class);
    }

    /**
     * Adds or updates a user interaction for a course, adjusting review likes as necessary.
     *
     * @param interactionDto The interaction data transfer object.
     * @return An updated or new {@link InteractionDto}.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "{#interactionDto.courseId, #interactionDto.type}", condition = "#interactionDto.type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", key = "{#interactionDto.instructorId, #interactionDto.type}", condition = "#interactionDto.type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true, condition = "#interactionDto.type.equals('course')"),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true, condition = "#interactionDto.type.equals('instructor')"),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public InteractionDto addOrUpdateInteraction(InteractionDto interactionDto) {
        // Check if interaction already exists
        Optional<Interaction> existingInteraction = Optional.empty();
        if(interactionDto.getType().equals("course")) {
            existingInteraction = interactionRepository.findByCourseIdAndUserIdAndReferrerAndType(
                    interactionDto.getCourseId(), interactionDto.getUserId(), interactionDto.getReferrer(), "course");
        } else if(interactionDto.getType().equals("instructor")) {
            existingInteraction = interactionRepository.findByInstructorIdAndUserIdAndReferrerAndType(
                    interactionDto.getInstructorId(), interactionDto.getUserId(), interactionDto.getReferrer(), "instructor");
        }

        int likesChange = 0;
        Interaction interaction;

        if (existingInteraction.isPresent()) {
            interaction = existingInteraction.get();
            Interaction.InteractionKind currentKind = interaction.getKind();
            Interaction.InteractionKind newKind = Interaction.InteractionKind.fromValue(interactionDto.getKind());

            if (!currentKind.equals(newKind)) {
                // Logic to adjust likes based on the change
                if (currentKind == Interaction.InteractionKind.LIKE && newKind == Interaction.InteractionKind.DISLIKE) {
                    likesChange = -2; // Switching from like to dislike
                } else if (currentKind == Interaction.InteractionKind.DISLIKE && newKind == Interaction.InteractionKind.LIKE) {
                    likesChange = 2; // Switching from dislike to like
                }

                interaction.setKind(newKind); // Update the kind of interaction
            }
        } else {
            // Create new interaction
            interaction = new Interaction()
                    .setKind(Interaction.InteractionKind.fromValue(interactionDto.getKind()))
                    .setType(interactionDto.getType())
                    .setCourseId(interactionDto.getCourseId())
                    .setInstructorId(interactionDto.getInstructorId())
                    .setUserId(interactionDto.getUserId())
                    .setReferrer(interactionDto.getReferrer());

            // Determine likesChange for new interaction
            likesChange = interaction.getKind() == Interaction.InteractionKind.LIKE ? 1 : -1;
        }

        interaction = interactionRepository.save(interaction);

        // Update likes count on the review only if there is a change
        if (likesChange != 0) {
            adjustReviewLikes(interactionDto.getCourseId(), interactionDto.getInstructorId(), interactionDto.getUserId(), likesChange, interactionDto.getType());
        }

        return InteractionMapper.toDto(interaction);
    }

    /**
     * Deletes a specific interaction for a user based on the course and referrer, adjusting review likes accordingly.
     *
     *  @param interactionDto The interaction data transfer object.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "{#interactionDto.courseId, #interactionDto.type}", condition = "#interactionDto.type.equals('course')"),
            @CacheEvict(value = "instructorReviewsCache", key = "{#interactionDto.instructorId, #interactionDto.type}", condition = "#interactionDto.type.equals('instructor')"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true, condition = "#interactionDto.type.equals('course')"),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true, condition = "#interactionDto.type.equals('instructor')"),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteInteraction(InteractionDto interactionDto) {
        Query query = new Query();
        if(interactionDto.getType().equals("course")) {
            query = new Query(Criteria.where("courseId").is(interactionDto.getCourseId())
                    .and("userId").is(interactionDto.getUserId())
                    .and("referrer").is(interactionDto.getReferrer())
                    .and("type").is("course"));
        } else if(interactionDto.getType().equals("instructor")) {
            query = new Query(Criteria.where("instructorId").is(interactionDto.getInstructorId())
                    .and("userId").is(interactionDto.getUserId())
                    .and("referrer").is(interactionDto.getReferrer())
                    .and("type").is("instructor"));
        }

        Interaction interaction = mongoTemplate.findAndRemove(query, Interaction.class);
        if (interaction != null) {
            adjustReviewLikes(interaction.getCourseId(), interaction.getInstructorId(), interactionDto.getUserId(), interaction.getKind() == Interaction.InteractionKind.LIKE ? -1 : 1, interactionDto.getType());
        }
    }

    /**
     * Deletes all interactions for a user based on the course.
     *
     * @param id The ID of the review.
     * @param userId   The ID of the user.
     * @param type The type of the interaction.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "{#id, #type}"),
            @CacheEvict(value = "instructorReviewsCache", key = "{#id, #type}"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true, condition = "#type.equals('course')"),
            @CacheEvict(value = "instructorsCacheWithFilters", allEntries = true, condition = "#type.equals('instructor')"),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteInteractions(String id, String userId, String type) {
        Review review = mongoTemplate.findById(id, Review.class);
        if (review == null) {
            return;
        }

        Query query = new Query(Criteria.where("instructorId").is(review.getInstructorId()).and("courseId").is(review.getCourseId()).and("userId").is(userId).and("type").is(review.getType()));
        mongoTemplate.remove(query, Interaction.class);

    }

    /**
     * Retrieves all interactions for a user which is the referrer in this case.
     *
     * @param referrer The referrer identifier.
     * @return A list of {@link InteractionDto} objects.
     */
    @Override
    public List<InteractionDto> getUserInteractions(String referrer) {
        return interactionRepository.findByReferrer(referrer).stream()
                .map(InteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CustomExceptionFactory.throwCustomException(entityType, exceptionType, args);
    }
}
