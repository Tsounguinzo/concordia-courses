package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.model.Subscription;

public class SubscriptionMapper {
    public static SubscriptionDto toDto(Subscription subscription) {
        return new SubscriptionDto()
                .setCourseId(subscription.getCourseId())
                .setUserId(subscription.getUserId());
    }
}
