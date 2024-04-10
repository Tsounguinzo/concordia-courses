package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.InteractionMapper;
import courses.concordia.dto.model.course.InteractionDto;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.INTERACTION;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Service
public class InteractionServiceImpl implements InteractionService {
    private final InteractionRepository interactionRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Retrieves the kind of interaction (e.g., LIKE, DISLIKE) a user has with a course based on a referrer.
     *
     * @param courseId  The ID of the course.
     * @param userId    The ID of the user.
     * @param referrer  The referrer identifier.
     * @return The kind of interaction.
     * @throws RuntimeException if the interaction is not found.
     */
    @Override
    public String getInteractionKind(String courseId, String userId, String referrer) {
        return interactionRepository.findByCourseIdAndUserIdAndReferrer(courseId, userId, referrer)
                .map(interaction -> InteractionMapper.toDto(interaction).getKind())
                .orElseThrow(() -> exception(INTERACTION, ENTITY_NOT_FOUND));
    }

    /**
     * Fetches all interactions a user has for a specific course and referrer.
     *
     * @param courseId The ID of the course.
     * @param referrer The referrer identifier.
     * @return A list of {@link InteractionDto} objects.
     */
    @Override
    public List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer) {
        return interactionRepository.findByCourseIdAndReferrer(courseId, referrer).stream()
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
    private void adjustReviewLikes(String courseId, String userId, int likesChange) {
        Query reviewQuery = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId));
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
            @CacheEvict(value = "courseReviewsCache", key = "#interactionDto.courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public InteractionDto addOrUpdateInteraction(InteractionDto interactionDto) {
        // Check if interaction already exists
        Optional<Interaction> existingInteraction = interactionRepository.findByCourseIdAndUserIdAndReferrer(
                interactionDto.getCourseId(), interactionDto.getUserId(), interactionDto.getReferrer());

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
                    .setCourseId(interactionDto.getCourseId())
                    .setUserId(interactionDto.getUserId())
                    .setReferrer(interactionDto.getReferrer());

            // Determine likesChange for new interaction
            likesChange = interaction.getKind() == Interaction.InteractionKind.LIKE ? 1 : -1;
        }

        interaction = interactionRepository.save(interaction);

        // Update likes count on the review only if there is a change
        if (likesChange != 0) {
            adjustReviewLikes(interactionDto.getCourseId(), interactionDto.getUserId(), likesChange);
        }

        return InteractionMapper.toDto(interaction);
    }

    /**
     * Deletes a specific interaction for a user based on the course and referrer, adjusting review likes accordingly.
     *
     * @param courseId The ID of the course.
     * @param userId   The ID of the user.
     * @param referrer The referrer identifier.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteInteraction(String courseId, String userId, String referrer) {
        Query query = new Query(Criteria.where("courseId").is(courseId)
                .and("userId").is(userId)
                .and("referrer").is(referrer));
        Interaction interaction = mongoTemplate.findAndRemove(query, Interaction.class);
        if (interaction != null) {
            adjustReviewLikes(courseId, userId, interaction.getKind() == Interaction.InteractionKind.LIKE ? -1 : 1);
        }
    }

    /**
     * Deletes all interactions for a user based on the course.
     *
     * @param courseId The ID of the course.
     * @param userId   The ID of the user.
     */
    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true),
            @CacheEvict(value = "reviewsCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteInteractions(String courseId, String userId) {
        Query query = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId));
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
