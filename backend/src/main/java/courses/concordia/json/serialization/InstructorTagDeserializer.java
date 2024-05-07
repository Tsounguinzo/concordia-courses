package courses.concordia.json.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import courses.concordia.model.Instructor;

import java.lang.reflect.Type;

public class InstructorTagDeserializer implements JsonDeserializer<Instructor.Tag> {
    @Override
    public Instructor.Tag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instructor.Tag.fromString(json.getAsString());
    }
}
