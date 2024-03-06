package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.NotificationMapper;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.model.Notification;
import courses.concordia.model.Review;
import courses.concordia.model.Subscription;
import courses.concordia.repository.NotificationRepository;
import courses.concordia.repository.SubscriptionRepository;
import courses.concordia.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MongoTemplate mongoTemplate;
    @Override
    public List<NotificationDto> getNotifications(String userId) {
        log.info("Fetching notifications for user ID: {}", userId);
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        notifications.sort((a, b) -> b.getReview().getTimestamp().compareTo(a.getReview().getTimestamp()));
        return notifications.stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void addNotifications(Review review) {
        List<Subscription> subscriptions = subscriptionRepository.findByCourseId(review.getCourseId());
        List<Notification> notifications = subscriptions.stream()
                .filter(subscription -> !subscription.getUserId().equals(review.getUserId()))
                .map(subscription -> new Notification()
                        .setReview(review)
                        .setUserId(subscription.getUserId())
                        .setSeen(false))
                .collect(Collectors.toList());

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    @Override
    public void deleteNotification(String userId, String courseId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("review.courseId").is(courseId));
        mongoTemplate.remove(query, Notification.class);
    }

    @Override
    public void updateNotifications(String creatorId, String courseId, Review review) {
        Query query = new Query(Criteria.where("review.userId").is(creatorId).and("review.courseId").is(courseId));
        Update update = new Update().set("review", review).set("seen", false);
        mongoTemplate.updateMulti(query, update, Notification.class);
    }

    @Override
    public void updateNotification(String userId, String courseId, String creatorId, boolean seen) {
        Query query = new Query(Criteria.where("userId").is(userId).and("review.courseId").is(courseId).and("review.userId").is(creatorId));
        Update update = new Update().set("seen", seen);
        mongoTemplate.updateFirst(query, update, Notification.class);
    }
}
