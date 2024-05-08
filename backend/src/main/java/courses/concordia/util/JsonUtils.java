package courses.concordia.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;

@Slf4j
public class JsonUtils {
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

    public static <T> T getData(InputStream inputStream, TypeToken<T> typeToken) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            Type dataType = typeToken.getType();
            return gson.fromJson(reader, dataType);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON from input stream", e);
        } catch (Exception e) {
            log.error("Unexpected error reading JSON from input stream", e);
        }
        return null;
    }

    public static <T> T getData(String jsonString, TypeToken<T> typeToken) {
        if (jsonString == null) {
            log.error("Input JSON string is null.");
            return null;
        }
        try {
            Type dataType = typeToken.getType();
            return gson.fromJson(jsonString, dataType);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse JSON from {}: {}", jsonString, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error reading JSON from {}: {}", jsonString, e.getMessage());
        }
        return null;
    }

    public static <T> void toJson(T data, String fileName) {
        String json = gson.toJson(data);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            log.info("Successfully saved JSON in {}", fileName);
        } catch (Exception e) {
            log.error("Failed to write JSON to file. File: {}, Error: {}", fileName, e.getMessage(), e);
        }
    }

    public static <T> String toJson(T data) {
        return gson.toJson(data);
    }
}
