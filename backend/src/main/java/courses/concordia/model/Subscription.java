package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "subscriptions")
public class Subscription {
    @MongoId
    private String _id;
    private String courseId;
    private String userId;
}
