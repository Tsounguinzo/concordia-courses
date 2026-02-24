package courses.concordia.dto.model.home;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HomeStatsDto {
    private long totalCourses;
    private long totalReviews;
    private long totalInstructors;
}
