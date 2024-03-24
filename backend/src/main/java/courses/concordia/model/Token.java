package courses.concordia.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Random;

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
        this.userId = user.get_id();
        this.createTime = LocalDateTime.now();
        this.expireTime = createTime.plusMinutes(5);
        this.token = tokenGenerator();
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }

    public String tokenGenerator(){
        Random random = new Random();
        StringBuilder str= new StringBuilder();
        for(int i = 0; i < 6; i ++){
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
