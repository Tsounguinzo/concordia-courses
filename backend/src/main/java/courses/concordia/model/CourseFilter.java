package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseFilter {
    private List<String> levels;
    private String query;
    private List<String> subjects;
    private List<String> terms;
    private CourseSort sortBy;

    @Data
    @AllArgsConstructor
    public static class CourseSort {
        private CourseSortType sortType;
        private boolean reverse;
    }

    @Getter
    @AllArgsConstructor
    public enum CourseSortType {
        Difficulty("difficulty"),
        Rating("rating"),
        ReviewCount("reviewCount");
        private final String value;
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }
}
