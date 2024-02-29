package courses.concordia.repository;

import courses.concordia.model.Interaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InteractionRepository extends MongoRepository<Interaction, String> {}
