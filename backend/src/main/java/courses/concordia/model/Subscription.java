package courses.concordia.model;

import javax.persistence.Id;

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
@Document(collection = "subscriptions")
public class Subscription {
    @MongoId
    private String _id;
    private String courseId;
    private String userId;
}
