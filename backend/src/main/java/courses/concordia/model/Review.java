package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.sql.Timestamp;
import java.util.List;

@Data
@Document(collection = "reviews")
public class Review {
    @MongoId
    private String _id;
    private String content;
    private String courseId;
    private List<String> instructors;
    private int rating; // 0-5
    private int difficulty;// 0-5
    private Timestamp timestamp;
    private String userId;
    private int likes;
}
