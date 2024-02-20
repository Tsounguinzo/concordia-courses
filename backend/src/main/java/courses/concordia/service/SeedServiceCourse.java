package courses.concordia.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import courses.concordia.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        List<Course> data = JsonUtil.getData(path, new TypeToken<List<Course>>(){});

        return data == null ? Collections.emptyList() : data;
    }
}
