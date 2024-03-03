package courses.concordia.repository;

import courses.concordia.model.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InteractionRepository extends MongoRepository<Interaction, String> {
    List<Interaction> findByCourseIdAndReferrer(String courseId, String referrer);
    Optional<Interaction> findByCourseIdAndUserIdAndReferrer(String courseId, String userId, String referrer);
}
