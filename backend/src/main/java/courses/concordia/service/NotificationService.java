package courses.concordia.service;

import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.UpdateNotificationDto;
import courses.concordia.model.Notification;
import courses.concordia.model.Review;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getNotifications(String userId);

    void updateNotification(UpdateNotificationDto updateNotificationDto);
    void addNotification(Review review);

    void deleteNotification(NotificationDto notification);
}
