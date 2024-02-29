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
public class InteractionDto {
    private InteractionKind kind;
    private String courseId;
    private String userId;
    private String referrer;

    @Getter
    @AllArgsConstructor
    private enum InteractionKind {
        LIKE("like"),
        DISLIKE("dislike");
        private final String value;
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }
}
