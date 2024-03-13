package courses.concordia.service;

import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.UpdateNotificationDto;
import courses.concordia.model.Notification;
import courses.concordia.model.Review;

import java.util.List;

public interface NotificationService {
    void addNotifications(Review review);
    List<NotificationDto> getNotifications(String userId);
    void updateNotifications(String creatorId, String courseId, Review review);
    void deleteNotification(String creatorId, String userId, String courseId);
    void updateNotification(String userId, String courseId, String creatorId, boolean seen);
}
