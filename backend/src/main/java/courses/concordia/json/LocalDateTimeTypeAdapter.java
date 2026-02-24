package courses.concordia.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>
{
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type srcType,
            JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(localDateTime.atOffset(ZoneOffset.UTC)));
    }
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException
    {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (json.isJsonPrimitive()) {
            return parseDateValue(json.getAsString());
        }

        if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            JsonElement dateElement = object.get("$date");
            if (dateElement == null || dateElement.isJsonNull()) {
                return null;
            }

            if (dateElement.isJsonPrimitive()) {
                return parseDateValue(dateElement.getAsString());
            }

            if (dateElement.isJsonObject()) {
                JsonElement numberLong = dateElement.getAsJsonObject().get("$numberLong");
                if (numberLong != null && numberLong.isJsonPrimitive()) {
                    long epochMillis = Long.parseLong(numberLong.getAsString());
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
                }
            }
        }

        throw new JsonParseException("Unsupported LocalDateTime format: " + json);
    }

    private LocalDateTime parseDateValue(String value) {
        try {
            return OffsetDateTime.parse(value, formatter).toLocalDateTime();
        } catch (Exception ignored) {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
