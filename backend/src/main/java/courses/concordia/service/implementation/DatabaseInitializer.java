package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import courses.concordia.model.GradeDistribution;
import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.GradeDistributionRepository;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.util.JsonUtils;
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

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final GradeDistributionRepository gradeDistributionRepository;
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
                    .forEach(this::processSeedFile);
        } catch (IOException e) {
            log.error("Failed to access or process seed directory: {}", seedDirPath, e);
        }
    }

    private void processSeedFile(Path path) {
        if (path.toString().endsWith("courses.json")) {
            processCourseFile(path);
        } else if (path.toString().endsWith("instructors.json")) {
            processInstructorFile(path);
        } else if (path.toString().endsWith("reviews.json")) {
            processReviewFile(path);
        } else if (path.toString().endsWith("distribution.json")) {
            gradesDistributionFile(path);
        }
    }

    private void gradesDistributionFile(Path path) {
        try {
            List<GradeDistribution> gradeDistributions = JsonUtils.getData(path, new TypeToken<List<GradeDistribution>>(){});
            gradeDistributionRepository.deleteAll();
            gradeDistributionRepository.saveAll(gradeDistributions);
            log.info("Successfully loaded and saved {} grade distributions from {}", gradeDistributions.size(), path);
        } catch (Exception e) {
            log.error("Failed to load grade distributions from {}: {}", path, e.getMessage(), e);
        }
    }

    private void processCourseFile(Path path) {
        try {
            List<Course> courses = JsonUtils.getData(path, new TypeToken<List<Course>>(){});
            courseRepository.deleteAll();
            courseRepository.saveAll(courses);
            log.info("Successfully loaded and saved {} courses from {}", courses.size(), path);
        } catch (Exception e) {
            log.error("Failed to load courses from {}: {}", path, e.getMessage(), e);
        }
    }

    private void processInstructorFile(Path path) {
        try {
            List<Instructor> instructors = JsonUtils.getData(path, new TypeToken<List<Instructor>>(){});
            instructorRepository.deleteAll();
            instructorRepository.saveAll(instructors);
            log.info("Successfully loaded and saved {} instructors from {}", instructors.size(), path);
        } catch (Exception e) {
            log.error("Failed to load instructors from {}: {}", path, e.getMessage(), e);
        }
    }

    private void processReviewFile(Path path) {
        try {
            List<Review> reviews = JsonUtils.getData(path, new TypeToken<List<Review>>(){});
            reviewRepository.deleteAll();
            reviewRepository.saveAll(reviews);
            log.info("Successfully loaded and saved {} reviews from {}", reviews.size(), path);
        } catch (Exception e) {
            log.error("Failed to load reviews from {}: {}", path, e.getMessage(), e);
        }
    }
}