package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "users")
public class User {
    @MongoId
    private String _id;
    private String email;
    private String firstName;
    private String lastName;
}
