package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.NotificationMapper;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Notification;
import courses.concordia.repository.NotificationRepository;
import courses.concordia.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.NOTIFICATION;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    @Override
    public List<NotificationDto> getNotifications(String userId) {
        log.info("Fetching notifications for user ID: {}", userId);
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notifications.sort((a, b) -> b.getReview().getTimestamp().compareTo(a.getReview().getTimestamp()));
        return notifications.stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void updateNotification(NotificationDto notificationDto) {
        log.info("Updating notification: {}", notificationDto);
        Optional<Notification> notification = notificationRepository.findById(notificationDto.get_id());
        if (notification.isPresent()) {
            notificationRepository.delete(notification.get());
        } else {
            throw exception(NOTIFICATION, ENTITY_NOT_FOUND);
        }
    }

    @Override
    public void deleteNotification(NotificationDto notificationDto) {
        log.info("Deleting notification: {}", notificationDto);
        Optional<Notification> notification = notificationRepository.findById(notificationDto.get_id());
        if (notification.isPresent()) {
            notificationRepository.delete(notification.get());
        } else {
            throw exception(NOTIFICATION, ENTITY_NOT_FOUND);
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
