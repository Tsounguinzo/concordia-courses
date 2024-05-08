package courses.concordia.dto.model.review;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import courses.concordia.model.Instructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewDto {
    private String _id;
    private String type; // course, instructor, school
    private String content;
    private LocalDateTime timestamp;
    private LocalDateTime adminReviewedAt;
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
