package courses.concordia.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseFilter {
    private List<String> levels;
    private String query;
    private List<String> subjects;
    private List<String> terms;
    private List<CourseSort> sortBy;

    @Data
    @AllArgsConstructor
    public static class CourseSort {
        private CourseSortType sortType;
        private boolean reverse;
    }

    public enum CourseSortType {
        Difficulty,
        Rating,
        ReviewCount,
    }
}
