package courses.concordia.dto.model.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
public class ReviewFilterDto {
    private ReviewSortType sortBy;
    @Getter
    @AllArgsConstructor
    public enum ReviewSortType {
        Experience("experience"),
        Date("date"),
        Likes("likes");
        private final String value;
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }
}
