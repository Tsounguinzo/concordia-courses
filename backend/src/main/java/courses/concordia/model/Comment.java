package courses.concordia.model;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @JsonAdapter(CommentIdAdapter.class)
    private String _id;
    private String userId;
    private String content;
    private LocalDateTime timestamp;

    static class CommentIdAdapter implements JsonDeserializer<String> {
        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }

            if (json.isJsonPrimitive()) {
                return json.getAsString();
            }

            if (json.isJsonObject() && json.getAsJsonObject().has("$oid")) {
                JsonElement oid = json.getAsJsonObject().get("$oid");
                return oid != null && oid.isJsonPrimitive() ? oid.getAsString() : json.toString();
            }

            return json.toString();
        }
    }
}
