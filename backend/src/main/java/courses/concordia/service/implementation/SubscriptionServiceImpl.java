package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.SubscriptionMapper;
import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.exception.CustomExceptionFactory;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Subscription;
import courses.concordia.repository.SubscriptionRepository;
import courses.concordia.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.SUBSCRIPTION;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;


@RequiredArgsConstructor
@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    /**
     * Retrieves all subscriptions for a specific user.
     *
     * @param userId The ID of the user whose subscriptions are being fetched.
     * @return A List of SubscriptionDto representing the user's subscriptions.
     */
    @Override
    public List<SubscriptionDto> getSubscriptions(String userId) {
        log.info("Fetching subscriptions for user ID: {}", userId);
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(SubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new subscription for a user to a course.
     *
     * @param userId    The ID of the user subscribing to the course.
     * @param courseId  The ID of the course to which the user is subscribing.
     * @return SubscriptionDto representing the newly created subscription.
     */
    @Override
    public SubscriptionDto addSubscription(String userId, String courseId) {
        log.info("Adding subscription for user ID: {} to course {}", userId, courseId);
        Subscription subscription = subscriptionRepository.save(new Subscription()
                .setCourseId(courseId)
                .setUserId(userId));
        return SubscriptionMapper.toDto(subscription);
    }

    /**
     * Deletes a subscription for a user from a specific course.
     *
     * @param userId    The ID of the user whose subscription is to be deleted.
     * @param courseId  The ID of the course from which the subscription is to be deleted.
     * @throws RuntimeException if the subscription is not found.
     */
    @Override
    public void deleteSubscription(String userId, String courseId) {
        log.info("Deleting subscription for user ID: {} to course {}", userId, courseId);
        subscriptionRepository.findByUserIdAndCourseId(userId, courseId)
                .ifPresentOrElse(
                        subscriptionRepository::delete,
                        () -> { throw exception(SUBSCRIPTION, ENTITY_NOT_FOUND); }
                );
    }

    /**
     * Retrieves a subscription for a user to a specific course.
     *
     * @param userId    The ID of the user whose subscription is being fetched.
     * @param courseId  The ID of the course to which the subscription relates.
     * @return SubscriptionDto representing the subscription if found, or null if not found.
     */
    @Override
    public SubscriptionDto getSubscription(String userId, String courseId) {
        log.info("Fetching subscription for user ID: {} to course {}", userId, courseId);
        return subscriptionRepository.findByUserIdAndCourseId(userId, courseId)
                .map(SubscriptionMapper::toDto)
                .orElse(null);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CustomExceptionFactory.throwCustomException(entityType, exceptionType, args);
    }
}
