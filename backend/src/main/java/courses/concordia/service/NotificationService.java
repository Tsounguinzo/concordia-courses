package courses.concordia.service;

import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.ReviewDto;

import java.util.List;

public interface NotificationService {
    void addNotifications(ReviewDto reviewDto);
    List<NotificationDto> getNotifications(String userId);
    void updateNotifications(String creatorId, String courseId, ReviewDto reviewDto);
    void deleteNotification(String creatorId, String userId, String courseId);
    void updateNotification(String userId, String courseId, String creatorId, boolean seen);
}
