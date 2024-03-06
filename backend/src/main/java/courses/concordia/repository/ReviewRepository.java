package courses.concordia.repository;

import courses.concordia.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findAllByCourseId(String courseId);
    List<Review> findAllByUserId(String userId);
    List<Review> findAllByInstructor(String instructor);
    List<Review> findAllByCourseIdAndUserId(String courseId, String userId);
}
