package courses.concordia.repository;

import courses.concordia.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenRepository extends MongoRepository<Token, Long> {
    Token findByToken(String token);
}
