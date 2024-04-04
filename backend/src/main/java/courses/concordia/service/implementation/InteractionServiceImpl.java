package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.InteractionMapper;
import courses.concordia.dto.model.course.InteractionDto;
import courses.concordia.exception.CCException;
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

    @Override
    public String getInteractionKind(String courseId, String userId, String referrer) {
        Optional<Interaction> interaction = interactionRepository.findByCourseIdAndUserIdAndReferrer(courseId, userId, referrer);

        if (interaction.isPresent()) {
            return InteractionMapper.toDto(interaction.get()).getKind();
        }

        throw exception(INTERACTION, ENTITY_NOT_FOUND);
    }

    @Override
    public List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer) {
        return interactionRepository.findByCourseIdAndReferrer(courseId, referrer).stream()
                .map(InteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    private void adjustReviewLikes(String courseId, String userId, int likesChange) {
        Query reviewQuery = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId));
        Update update = new Update().inc("likes", likesChange);
        mongoTemplate.updateFirst(reviewQuery, update, Review.class);
    }

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#interactionDto.courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
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

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
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

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteInteractions(String courseId, String userId) {
        Query query = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId));
        mongoTemplate.remove(query, Interaction.class);
    }

    @Override
    public List<Interaction> interactionsForReview(String courseId, String userId) {
        return mongoTemplate.find(
                new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId)),
                Interaction.class);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
