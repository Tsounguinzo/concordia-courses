package courses.concordia.service;

import courses.concordia.model.Course;
import courses.concordia.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DatabaseInitializer {

    private final SeedServiceCourse seedServiceCourse;
    private final CourseRepository courseRepository;
    private final Environment environment;

    @Value("${app.init-db:false}")
    private boolean initDb;

    @PostConstruct
    public void init() {
        // Check if the initializer should run
        if (!initDb) {
            log.info("Database initialization is disabled.");
            return;
        }

        // Check for development profile
        if (!isProfileActive("dev")) {
            log.info("Database initialization skipped in non-development profiles.");
            return;
        }

        log.info("Starting database initialization...");

        Path seedDir = Paths.get("backend","src", "main", "resources", "seeds");
        try (var files = Files.walk(seedDir)){
            files.filter(Files::isRegularFile)
                    .forEach(path -> {
                        if (path.toString().endsWith("courses.json")) {
                            List<Course> courses = seedServiceCourse.readSeedFromFile(path);
                            courseRepository.deleteAll();
                            courseRepository.saveAll(courses);
                            log.info("Loaded and saved {} courses from {}", courses.size(), path);
                        }
                    });
        } catch (IOException e) {
            log.error("Error accessing seed directory: {}", e.getMessage());
        }
    }

    private boolean isProfileActive(String profile) {
        return List.of(environment.getActiveProfiles()).contains(profile);
    }
}