package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "instructors")
public class Instructor {
    @MongoId
    private String _id;
    private String name;
    private String term;
}
