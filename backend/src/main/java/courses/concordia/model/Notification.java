package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "notifications")
public class Notification {
    @MongoId
    private String _id;
    private Review review;
    private boolean seen;
    private String userId;
}
