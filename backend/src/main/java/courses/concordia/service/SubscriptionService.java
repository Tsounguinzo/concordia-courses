package courses.concordia.service;

import courses.concordia.dto.model.subscription.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto addSubscription(String userId, String courseId);

    void deleteSubscription(String userId, String courseId);

    SubscriptionDto getSubscription(String userId, String courseId);

    List<SubscriptionDto> getSubscriptions(String userId);
}
