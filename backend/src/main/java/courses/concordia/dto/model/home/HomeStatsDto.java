package courses.concordia.dto.model.home;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeStatsDto {
    private long totalCourses;
    private long totalReviews;
    private long totalInstructors;
}
