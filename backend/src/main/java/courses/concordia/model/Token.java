package courses.concordia.model;

import courses.concordia.service.TokenGenerator;
import courses.concordia.service.implementation.token.NumericTokenGenerator;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
@Document(collection = "tokens")
public class Token {
    @MongoId
    private String _id;
    private String userId;
    private String token;
    @CreatedDate
    private LocalDateTime createTime;
    private LocalDateTime expireTime;


    public Token(User user){
        this(user, new NumericTokenGenerator());
    }

    public Token(User user, TokenGenerator tokenGenerator) {
        this(user, tokenGenerator, 5);
    }

    public Token(User user, TokenGenerator tokenGenerator, int tokenExpirationTimeInMinutes) {
        this.userId = user.get_id();
        this.createTime = LocalDateTime.now();
        this.expireTime = createTime.plusMinutes(tokenExpirationTimeInMinutes);
        this.token = tokenGenerator.generateToken();
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
