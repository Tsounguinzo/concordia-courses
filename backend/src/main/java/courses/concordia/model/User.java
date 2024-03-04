package courses.concordia.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "users")
public class User {
    @MongoId
    private String username;
    private String email;
    private String password;
}
