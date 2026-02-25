package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import courses.concordia.model.GradeDistribution;
import courses.concordia.model.Instructor;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.GradeDistributionRepository;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.util.JsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class DatabaseInitializer {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final GradeDistributionRepository gradeDistributionRepository;
    private final Environment environment;
    private final CacheManager cacheManager;

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
        List<Path> seedDirectories = resolveSeedDirectories();
        if (seedDirectories.isEmpty()) {
            log.error("No seed directories were found. Checked configured path: {}", seedDirPath);
            return;
        }

        log.info("Seeding database from directories: {}", seedDirectories);
        List<Path> allSeedFiles = new ArrayList<>();

        for (Path directory : seedDirectories) {
            try (var files = Files.walk(directory)) {
                files.filter(Files::isRegularFile)
                        .forEach(allSeedFiles::add);
            } catch (IOException e) {
                log.error("Failed to access or process seed directory: {}", directory, e);
            }
        }

        if (allSeedFiles.isEmpty()) {
            log.warn("No seed files found in directories: {}", seedDirectories);
            return;
        }

        allSeedFiles.sort(Comparator.comparing(Path::toString));

        List<Path> courseFiles = allSeedFiles.stream().filter(this::isCourseFile).toList();
        List<Path> instructorFiles = allSeedFiles.stream().filter(this::isInstructorFile).toList();
        List<Path> distributionFiles = allSeedFiles.stream().filter(this::isDistributionFile).toList();

        courseFiles = preferDbFiles(courseFiles, "db.courses.json", "db.course.json");
        instructorFiles = preferDbFiles(instructorFiles, "db.instructors.json", "db.instructor.json");
        courseFiles = uniqueByFileName(courseFiles);
        instructorFiles = uniqueByFileName(instructorFiles);
        distributionFiles = uniqueByFileName(distributionFiles);

        processCourseFiles(courseFiles);
        processInstructorFiles(instructorFiles);
        processDistributionFiles(distributionFiles);
        evictSeedSensitiveCaches();
    }

    private List<Path> resolveSeedDirectories() {
        Set<Path> candidates = new LinkedHashSet<>();
        candidates.add(Paths.get(seedDirPath));
        candidates.add(Paths.get("backend", "src", "main", "resources", "seeds"));
        candidates.add(Paths.get("src", "main", "resources", "seeds"));
        candidates.add(Paths.get("backend", "src", "main", "resources", "data"));
        candidates.add(Paths.get("src", "main", "resources", "data"));

        return candidates.stream()
                .map(Path::normalize)
                .filter(Files::exists)
                .filter(Files::isDirectory)
                .toList();
    }

    private boolean isCourseFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith("courses.json") || fileName.equals("db.course.json");
    }

    private boolean isInstructorFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith("instructors.json") || fileName.equals("db.instructor.json");
    }

    private boolean isDistributionFile(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith("distribution.json");
    }

    private List<Path> preferDbFiles(List<Path> files, String pluralDbFile, String singularDbFile) {
        List<Path> preferred = files.stream()
                .filter(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    return fileName.equals(pluralDbFile) || fileName.equals(singularDbFile);
                })
                .toList();
        return preferred.isEmpty() ? files : preferred;
    }

    private List<Path> uniqueByFileName(List<Path> files) {
        Set<String> seen = new LinkedHashSet<>();
        List<Path> unique = new ArrayList<>();
        for (Path file : files) {
            String fileName = file.getFileName().toString().toLowerCase();
            if (seen.add(fileName)) {
                unique.add(file);
            }
        }
        return unique;
    }

    private void evictSeedSensitiveCaches() {
        List<String> cachesToEvict = List.of(
                "coursesCacheWithFilters",
                "coursesCache",
                "courseReviewsCache",
                "courseInstructorsCache",
                "instructorsCacheWithFilters",
                "instructorsCache",
                "instructorReviewsCache",
                "reviewsCacheWithFilters",
                "homeStatsCache",
                "gradeDistribution"
        );

        for (String cacheName : cachesToEvict) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
        log.info("Cleared seed-sensitive caches after database initialization.");
    }

    private void processDistributionFiles(List<Path> files) {
        if (files.isEmpty()) {
            return;
        }
        List<GradeDistribution> allDistributions = new ArrayList<>();
        try {
            for (Path path : files) {
                List<GradeDistribution> gradeDistributions = JsonUtils.getData(path, new TypeToken<List<GradeDistribution>>(){});
                allDistributions.addAll(gradeDistributions);
            }
            gradeDistributionRepository.deleteAll();
            gradeDistributionRepository.saveAll(allDistributions);
            log.info("Successfully loaded and saved {} grade distributions from {}", allDistributions.size(), files);
        } catch (Exception e) {
            log.error("Failed to load grade distributions from {}: {}", files, e.getMessage(), e);
        }
    }

    private void processCourseFiles(List<Path> files) {
        if (files.isEmpty()) {
            return;
        }
        List<Course> allCourses = new ArrayList<>();
        try {
            for (Path path : files) {
                List<Course> courses = JsonUtils.getData(path, new TypeToken<List<Course>>(){});
                allCourses.addAll(courses);
            }
            courseRepository.deleteAll();
            courseRepository.saveAll(allCourses);
            log.info("Successfully loaded and saved {} courses from {}", allCourses.size(), files);
        } catch (Exception e) {
            log.error("Failed to load courses from {}: {}", files, e.getMessage(), e);
        }
    }

    private void processInstructorFiles(List<Path> files) {
        if (files.isEmpty()) {
            return;
        }
        List<Instructor> allInstructors = new ArrayList<>();
        try {
            for (Path path : files) {
                List<Instructor> instructors = JsonUtils.getData(path, new TypeToken<List<Instructor>>(){});
                allInstructors.addAll(instructors);
            }
            instructorRepository.deleteAll();
            instructorRepository.saveAll(allInstructors);
            log.info("Successfully loaded and saved {} instructors from {}", allInstructors.size(), files);
        } catch (Exception e) {
            log.error("Failed to load instructors from {}: {}", files, e.getMessage(), e);
        }
    }

}
