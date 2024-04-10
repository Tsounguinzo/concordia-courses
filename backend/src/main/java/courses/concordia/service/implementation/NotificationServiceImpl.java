package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.NotificationMapper;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.model.Notification;
import courses.concordia.model.Review;
import courses.concordia.model.Subscription;
import courses.concordia.repository.NotificationRepository;
import courses.concordia.repository.SubscriptionRepository;
import courses.concordia.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    /**
     * Fetches all notifications for a given user, sorted by the timestamp of the associated reviews in descending order.
     *
     * @param userId The ID of the user whose notifications are being fetched.
     * @return A list of NotificationDto objects for the user.
     */
    @Override
    public List<NotificationDto> getNotifications(String userId) {
        log.info("Fetching notifications for user ID: {}", userId);
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        notifications.sort((a, b) -> b.getReview().getTimestamp().compareTo(a.getReview().getTimestamp()));
        return notifications.stream().map(NotificationMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Creates notifications for all subscribers of a course when a new review is posted,
     * except for the user who posted the review.
     *
     * @param reviewDto The review based on which notifications are created.
     */
    @Override
    public void addNotifications(ReviewDto reviewDto) {
        List<Subscription> subscriptions = subscriptionRepository.findByCourseId(reviewDto.getCourseId());
        List<Notification> notifications = subscriptions.stream()
                .filter(subscription -> !subscription.getUserId().equals(reviewDto.getUserId()))
                .map(subscription -> new Notification()
                        .setReview(modelMapper.map(reviewDto, Review.class))
                        .setUserId(subscription.getUserId())
                        .setSeen(false))
                .collect(Collectors.toList());

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    /**
     * Deletes a notification for a specific course created by a specific creator for a specific user.
     * If the creatorId is null, it targets notifications created by any user.
     *
     * @param creatorId The ID of the user who created the review triggering the notification.
     * @param userId    The ID of the user for whom the notification was created.
     * @param courseId  The ID of the course related to the notification.
     */
    @Override
    public void deleteNotification(String creatorId, String userId, String courseId) {
        if(creatorId == null && userId == null) return;

        Criteria criteria = Criteria.where("review.courseId").is(courseId);
        if (userId != null) criteria = criteria.and("userId").is(userId).and("review.userId").is(creatorId);
        if (creatorId != null) criteria = criteria.and("review.userId").is(creatorId);

        Query query = new Query(criteria);
        mongoTemplate.remove(query, Notification.class);
    }

    /**
     * Updates notifications based on a new review for a specific course by a specific creator.
     *
     * @param creatorId The ID of the creator of the review.
     * @param courseId  The ID of the course for which the review is written.
     * @param reviewDto The new review data to update the notification with.
     */
    @Override
    public void updateNotifications(String creatorId, String courseId, ReviewDto reviewDto) {
        Query query = new Query(Criteria.where("review.userId").is(creatorId).and("review.courseId").is(courseId));
        Update update = new Update().set("review", modelMapper.map(reviewDto, Review.class)).set("seen", false);
        mongoTemplate.updateMulti(query, update, Notification.class);
    }

    /**
     * Marks a specific notification as seen or unseen for a user regarding a course created by a specific creator.
     *
     * @param userId    The ID of the user for whom the notification is intended.
     * @param courseId  The ID of the course related to the notification.
     * @param creatorId The ID of the creator of the review triggering the notification.
     * @param seen      The new seen status of the notification.
     */
    @Override
    public void updateNotification(String userId, String courseId, String creatorId, boolean seen) {
        Query query = new Query(Criteria.where("userId").is(userId).and("review.courseId").is(courseId).and("review.userId").is(creatorId));
        Update update = new Update().set("seen", seen);
        mongoTemplate.updateFirst(query, update, Notification.class);
    }
}
