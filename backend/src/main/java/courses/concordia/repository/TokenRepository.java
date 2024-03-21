package courses.concordia.repository;

import courses.concordia.model.Token;
import courses.concordia.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);
}
