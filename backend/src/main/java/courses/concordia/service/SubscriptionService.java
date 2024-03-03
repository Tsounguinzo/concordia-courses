package courses.concordia.service;

import courses.concordia.dto.model.course.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionDto> getSubscriptions(SubscriptionDto subscriptionDto);

    SubscriptionDto addSubscription(SubscriptionDto subscriptionDto);

    void deleteSubscription(SubscriptionDto subscriptionDto);
}
