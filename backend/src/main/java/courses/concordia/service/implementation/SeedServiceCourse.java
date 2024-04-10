package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import courses.concordia.service.SeedService;
import courses.concordia.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SeedServiceCourse implements SeedService<Course> {
    /**
     * Reads a list of {@link Course} objects from a seed file located at the given path.
     * This method assumes the seed file is formatted as a JSON array of course objects.
     *
     * @param path The {@link Path} to the seed file.
     * @return A list of {@link Course} objects read from the file. If the file does not exist,
     *         or an error occurs during reading, an empty list is returned.
     */
    @Override
    public List<Course> readSeedFromFile(Path path) {

        if (Files.notExists(path)) {
            log.error("Seed file does not exist at path: {}", path);
            return Collections.emptyList();
        }
        List<Course> data = JsonUtils.getData(path, new TypeToken<List<Course>>(){});

        return data == null ? Collections.emptyList() : data;
    }
}
