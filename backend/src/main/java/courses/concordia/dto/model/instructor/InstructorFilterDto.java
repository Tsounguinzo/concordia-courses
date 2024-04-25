package courses.concordia.dto.model.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class InstructorFilterDto {
    private List<String> levels;
    private String query;
    private List<String> subjects;
    private List<String> departments;
    private List<String> tags;
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
