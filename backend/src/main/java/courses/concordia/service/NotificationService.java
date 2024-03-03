package courses.concordia.service;

import courses.concordia.dto.model.course.NotificationDto;

import java.util.List;

public interface NotificationService {
    List<NotificationDto> getNotifications(String userId);

    void updateNotification(NotificationDto notification);

    void deleteNotification(NotificationDto notification);
}
