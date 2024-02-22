package courses.concordia.dto;

import courses.concordia.model.Course;
import courses.concordia.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseReviewsDTO {
    private Course course;
    private List<Review> reviews;
}
