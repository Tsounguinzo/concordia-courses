package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Id;
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
    @Id
    private String _id;
    private String type; // course, instructor, school
    private String content;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date timestamp;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date adminReviewedAt;
    private boolean flagged;
    private int likes;
    private String userId;

    // common fields for course and instructor
    private int difficulty; // 0-5
    private String courseId;
    private String instructorId;

    // course specific fields
    private int experience; // 0-5

    // instructor specific fields
    private int rating; // 0-5
    private Set<Instructor.Tag> tags;

    // School specific ratings
    private int schoolId;
}
