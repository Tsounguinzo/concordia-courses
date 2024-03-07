package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.model.Notification;

public class NotificationMapper {
    public static NotificationDto toDto(Notification notification) {
        return new NotificationDto()
                .set_id(notification.get_id())
                .setUserId(notification.getUserId())
                .setReview(ReviewMapper.toDto(notification.getReview()))
                .setSeen(notification.isSeen());
    }
}
