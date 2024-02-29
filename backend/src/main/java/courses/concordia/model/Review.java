package courses.concordia.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Document(collection = "reviews")
public class Review {
    @MongoId
    private String _id;
    private String content;
    private String courseId;
    private String instructor;
    private int rating; // 0-5
    private int difficulty;// 0-5
    private Date timestamp;
    private String userId;
    private int likes;
}
