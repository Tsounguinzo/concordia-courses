package courses.concordia.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Path;

@Slf4j
public class JsonUtil {
    private static final Gson gson = new Gson();

    public static <T> T getData(Path jsonFilePath, TypeToken<T> typeToken) {
        try (Reader reader = new FileReader(jsonFilePath.toFile())) {
            Type dataType = typeToken.getType();
            return gson.fromJson(reader, dataType);
        } catch (FileNotFoundException e) {
            log.error("JSON file not found at {}: {}", jsonFilePath, e.getMessage());
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON from {}: {}", jsonFilePath, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error reading JSON from {}: {}", jsonFilePath, e.getMessage());
        }
        return null;
    }

    public static <T> T getData(String jsonFile, TypeToken<T> typeToken) {
        if (jsonFile == null) {
            log.error("Input JSON string is null.");
            return null;
        }
        try {
            Type dataType = typeToken.getType();
            return gson.fromJson(jsonFile, dataType);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON. Error: {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> void toJson(T data, String fileName) {
        String json = gson.toJson(data);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            log.info("Successfully wrote JSON to {}", fileName);
        } catch (Exception e) {
            log.error("Failed to write JSON to file. File: {}, Error: {}", fileName, e.getMessage(), e);
        }
    }
}
