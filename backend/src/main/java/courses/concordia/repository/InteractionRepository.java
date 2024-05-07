package courses.concordia.repository;

import courses.concordia.model.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String> {
    List<Interaction> findByReferrer(String referrer);
    Optional<Interaction> findByCourseIdAndUserIdAndReferrerAndType(String courseId, String userId, String referrer, String type);
    Optional<Interaction> findByInstructorIdAndUserIdAndReferrerAndType(String instructorId, String userId, String referrer, String type);
    List<Interaction> findByCourseIdAndReferrerAndType(String courseId, String referrer, String type);
    List<Interaction> findByInstructorIdAndReferrerAndType(String instructorId, String referrer, String type);
}
