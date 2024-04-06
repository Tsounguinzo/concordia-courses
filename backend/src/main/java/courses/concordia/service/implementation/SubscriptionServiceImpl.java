package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.SubscriptionMapper;
import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.exception.ExceptionHelper;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Subscription;
import courses.concordia.repository.SubscriptionRepository;
import courses.concordia.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.SUBSCRIPTION;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;


@RequiredArgsConstructor
@Slf4j
@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    @Override
    public List<SubscriptionDto> getSubscriptions(String userId) {
        log.info("Fetching subscriptions for user ID: {}", userId);
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(SubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto addSubscription(String userId, String courseId) {
        log.info("Adding subscription for user ID: {} to course {}", userId, courseId);
        Subscription subscription = subscriptionRepository.save(new Subscription()
                .setCourseId(courseId)
                .setUserId(userId));
        return SubscriptionMapper.toDto(subscription);
    }

    @Override
    public void deleteSubscription(String userId, String courseId) {
        log.info("Deleting subscription for user ID: {} to course {}", userId, courseId);
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndCourseId(userId, courseId);
        if (subscription.isPresent()) {
            subscriptionRepository.deleteByUserIdAndCourseId(userId, courseId);
        } else {
            throw exception(SUBSCRIPTION, ENTITY_NOT_FOUND);
        }
    }

    @Override
    public SubscriptionDto getSubscription(String userId, String courseId) {
        log.info("Fetching subscription for user ID: {} to course {}", userId, courseId);
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndCourseId(userId, courseId);
        return subscription.map(SubscriptionMapper::toDto).orElse(null);
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ExceptionHelper.throwException(entityType, exceptionType, args);
    }
}
