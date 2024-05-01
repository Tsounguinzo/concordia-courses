package courses.concordia.json.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import courses.concordia.model.Instructor;

import java.lang.reflect.Type;

public class InstructorDepartmentDeserializer implements JsonDeserializer<Instructor.Department> {
    @Override
    public Instructor.Department deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Instructor.Department.fromString(json.getAsString());
    }
}
