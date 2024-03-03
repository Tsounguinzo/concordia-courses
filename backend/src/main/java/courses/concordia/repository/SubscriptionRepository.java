package courses.concordia.repository;

import courses.concordia.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    List<Subscription> findByUserId(String userId);
    Optional<Subscription> findByUserIdAndCourseId(String userId, String courseId);
    void deleteByUserIdAndCourseId(String userId, String courseId);
}
