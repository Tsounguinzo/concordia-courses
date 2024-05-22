package courses.concordia.repository;

import courses.concordia.model.GradeDistribution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeDistributionRepository extends MongoRepository<GradeDistribution, String> {}