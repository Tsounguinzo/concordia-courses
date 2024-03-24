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
