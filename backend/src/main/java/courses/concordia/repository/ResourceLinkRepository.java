package courses.concordia.repository;

import courses.concordia.model.ResourceLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceLinkRepository extends MongoRepository<ResourceLink, String> {
}
