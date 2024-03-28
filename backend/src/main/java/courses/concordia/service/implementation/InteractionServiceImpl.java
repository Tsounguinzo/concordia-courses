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

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#interactionDto.courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
    })
    @Override
    public InteractionDto addInteraction(InteractionDto interactionDto) {
        Interaction interaction = new Interaction()
                .setKind(Interaction.InteractionKind.fromValue(interactionDto.getKind()))
                .setCourseId(interactionDto.getCourseId())
                .setUserId(interactionDto.getUserId())
                .setReferrer(interactionDto.getReferrer());
        interaction = interactionRepository.save(interaction);
        Query reviewQuery = new Query(Criteria.where("courseId").is(interactionDto.getCourseId()).and("userId").is(interactionDto.getUserId()));
        Update update = new Update();
        if (interaction.getKind() == Interaction.InteractionKind.LIKE) {
            update.inc("likes", 1);
        } else {
            update.inc("likes", -1);
        }
        mongoTemplate.updateFirst(reviewQuery, update, Review.class);
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
            Query reviewQuery = new Query(Criteria.where("courseId").is(courseId).and("userId").is(userId));
            Update update = new Update();
            if (interaction.getKind() == Interaction.InteractionKind.LIKE) {
                update.inc("likes", -1);
            } else {
                update.inc("likes", 1);
            }
            mongoTemplate.updateFirst(reviewQuery, update, Review.class);
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
