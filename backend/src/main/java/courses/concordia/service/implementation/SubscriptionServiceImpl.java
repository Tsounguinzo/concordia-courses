package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.SubscriptionMapper;
import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.exception.CCException;
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
    public List<SubscriptionDto> getSubscriptions(SubscriptionDto subscriptionDto) {
        log.info("Fetching subscriptions for user ID: {}", subscriptionDto.getUserId());
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(subscriptionDto.getUserId());
        return subscriptions.stream()
                .map(SubscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionDto addSubscription(SubscriptionDto subscriptionDto) {
        log.info("Adding subscription for user ID: {} to course {}", subscriptionDto.getUserId(), subscriptionDto.getCourseId());
        Subscription subscription = subscriptionRepository.save(new Subscription()
                .setCourseId(subscriptionDto.getCourseId())
                .setUserId(subscriptionDto.getUserId()));
        return SubscriptionMapper.toDto(subscription);
    }

    @Override
    public void deleteSubscription(SubscriptionDto subscriptionDto) {
        log.info("Deleting subscription for user ID: {} to course {}", subscriptionDto.getUserId(), subscriptionDto.getCourseId());
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndCourseId(subscriptionDto.getUserId(), subscriptionDto.getCourseId());
        if (subscription.isPresent()) {
            subscriptionRepository.deleteByUserIdAndCourseId(subscriptionDto.getUserId(), subscriptionDto.getCourseId());
        } else {
            throw exception(SUBSCRIPTION, ENTITY_NOT_FOUND);
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
