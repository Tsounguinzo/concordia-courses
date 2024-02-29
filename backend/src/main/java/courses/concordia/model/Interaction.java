package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "interactions")
public class Interaction {
    @MongoId
    private String _id;
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
