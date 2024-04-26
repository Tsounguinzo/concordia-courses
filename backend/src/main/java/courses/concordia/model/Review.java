package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "reviews")
public class Review {
    // common fields
    @MongoId
    private String _id;
    private String type; // course, instructor, school
    private String content;
    private Date timestamp;
    private Date adminReviewedAt;
    private boolean flagged;
    private int likes;
    private int thumbsDownTotal;
    private int thumbsUpTotal;

    // common fields for course and instructor
    private int difficulty; // 0-5
    private String userId;
    private String courseId;
    private String instructorId;

    // course specific fields
    private int experience; // 0-5

    // instructor specific fields
    private int rating; // 0-5
    private Set<Instructor.InstructorTag> tags;

    // School specific ratings
    private int internetRating; // 0-5
    private int locationRating; // 0-5
    private int opportunitiesRating; // 0-5
    private int reputationRating; // 0-5
    private int safetyRating; // 0-5
    private int socialRating; // 0-5
    private int foodRating; // 0-5
    private int happinessRating; // 0-5
    private int facilitiesRating; // 0-5
    private int clubsRating; // 0-5
}
