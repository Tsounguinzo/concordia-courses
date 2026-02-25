package courses.concordia.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class FlexibleStringTypeAdapter implements JsonDeserializer<String> {

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
