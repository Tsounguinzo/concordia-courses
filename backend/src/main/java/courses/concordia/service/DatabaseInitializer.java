package courses.concordia.service;

import courses.concordia.model.Course;
import courses.concordia.repository.CourseRepository;
import courses.concordia.service.implementation.SeedServiceCourse;
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
    @Value("${app.seed-dir:backend/src/main/resources/seeds}")
    private String seedDirPath;

    @PostConstruct
    public void init() {
        log.info("Database initialization process started.");
        try {
            if (!initDb) {
                log.info("Database initialization is disabled via configuration.");
                return;
            }

            if (!isProfileActive("dev")) {
                log.info("Database initialization is configured to run only in the 'dev' profile.");
                return;
            }

            log.info("Initializing database with seed data.");
            seedDatabase();
            log.info("Database initialization process completed successfully.");
        } catch (Exception e) {
            log.error("Database initialization process failed", e);
            throw e; // Rethrowing the exception to make it clear that initialization failed
        }
    }

    private boolean isProfileActive(String profile) {
        return List.of(environment.getActiveProfiles()).contains(profile);
    }

    private void seedDatabase() {
        log.info("Seeding database from directory: {}", seedDirPath);
        Path seedDir = Paths.get(seedDirPath);
        try (var files = Files.walk(seedDir)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("courses-v2.json"))
                    .forEach(this::processSeedFile);
        } catch (IOException e) {
            log.error("Failed to access or process seed directory: {}", seedDirPath, e);
        }
    }

    private void processSeedFile(Path path) {
        try {
            List<Course> courses = seedServiceCourse.readSeedFromFile(path);
            courseRepository.deleteAll();
            courseRepository.saveAll(courses);
            log.info("Successfully loaded and saved {} courses from {}", courses.size(), path);
        } catch (Exception e) {
            log.error("Failed to load courses from {}: {}", path, e.getMessage(), e);
        }
    }
}