package courses.concordia.dto.model.review;

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
public class ReviewSortingDto {
    private ReviewSortType sortType;
    private boolean reverse;

    @Getter
    @AllArgsConstructor
    public enum ReviewSortType {
        Recent("recent"),
        Rating("rating"),
        Experience("experience"),
        Difficulty("difficulty"),
        Likes("likes");
        private final String value;
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }
}
