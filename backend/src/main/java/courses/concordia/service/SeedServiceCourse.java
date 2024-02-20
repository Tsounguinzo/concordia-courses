package courses.concordia.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SeedServiceCourse implements SeedService<Course> {
    private final Gson gson = new Gson();

    @Override
    public List<Course> readSeedFromFile(Path path) {

        if (Files.notExists(path)) {
            log.error("Seed file does not exist at path: {}", path);
            return Collections.emptyList();
        }

        try (Reader reader = new FileReader(path.toFile())) {
            Type seedType = new TypeToken<List<Course>>(){}.getType();
            return gson.fromJson(reader, seedType);
        } catch (JsonSyntaxException e) {
            log.error("Failed to parse seed file at path: {}. The file format is incorrect.", path, e);
        } catch (Exception e) {
            log.error("An error occurred while reading the seed file at path: {}", path, e);
        }
        return Collections.emptyList();

    }
}
