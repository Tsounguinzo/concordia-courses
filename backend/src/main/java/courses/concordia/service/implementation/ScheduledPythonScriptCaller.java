package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.model.Review;
import courses.concordia.service.EmailService;
import courses.concordia.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScheduledPythonScriptCaller {
    private final EmailService emailService;
    private final ReviewServiceImpl reviewService;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String LAST_SCRAPED_DATE_KEY = "rmpLastScrapedDate";
    private static final String DEFAULT_CUTOFF_DATE = "2024-06-22";
    private static final String RESOURCE_DIRECTORY = "rmpScraping";
    private static final String SCRIPT_NAME = "processReviews.py";

    private static final String[] REQUIRED_FILES = {
            "processReviews.py",
            "utils.py",
            "concordiaProfRateMyProf.json",
            "subject-catalogs.json",
            "department_descriptions.json",
            "updated_ids.json"
    };

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        scrapeRMP();
    }
    @Scheduled(cron = "0 0 0 * * ?")  // Run daily at midnight
    public void scrapeRMP() {
        log.info("Starting to scrape RMP");
        int reviewsCount = 0;
        try {

            var start = getCurrentDateTime();

            String lastScrapedDate = getLastScrapedDate();
            String cutoffDate = lastScrapedDate == null ? DEFAULT_CUTOFF_DATE : lastScrapedDate;
            updateLastScrapedDate();

            File scriptFile = prepareScriptDirectory();
            String result = runPythonScript(scriptFile, cutoffDate);

            List<Review> reviews = JsonUtils.getData(result, new TypeToken<List<Review>>() {});

            if (reviews == null) {
                throw new IOException("Failed to parse reviews from JSON");
            }

            reviewsCount = reviews.size();
            addReviews(reviews);

            var end = getCurrentDateTime();
            emailService.sendSuccessMailMessage("Beaudelaire", "Beaudelaire@tutamail.com", start, end, reviewsCount);

        } catch (Exception e) {
            log.error("Error while scraping RMP", e);
            emailService.sendFailureMailMessage("Beaudelaire", "Beaudelaire@tutamail.com", e.getMessage());
        }
        log.info("Finished scraping RMP");
    }

    private String getLastScrapedDate() {
        String lastScrapedDate = stringRedisTemplate.opsForValue().get(LAST_SCRAPED_DATE_KEY);
        if (lastScrapedDate != null) {
            log.info("Scraping RMP with cut-off date: {}", lastScrapedDate);
        } else {
            log.info("No last scraped date found, scraping all reviews starting from {}", DEFAULT_CUTOFF_DATE);
        }
        return lastScrapedDate;
    }

    private void updateLastScrapedDate() {
        String currentDate = LocalDate.now().toString();
        stringRedisTemplate.opsForValue().set(LAST_SCRAPED_DATE_KEY, currentDate);
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

    private File prepareScriptDirectory() throws IOException {
        File tempDir = Files.createTempDirectory("rmp_scraping").toFile();
        tempDir.deleteOnExit();

        for (String fileName : REQUIRED_FILES) {
            ClassPathResource resource = new ClassPathResource(RESOURCE_DIRECTORY + "/" + fileName);
            if (!resource.exists()) {
                throw new FileNotFoundException("Resource not found: " + RESOURCE_DIRECTORY + "/" + fileName);
            }

            File destFile = new File(tempDir, fileName);
            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
            }
        }

        return tempDir;
    }

    private String runPythonScript(File scriptDir, String cutoffDate) throws IOException, InterruptedException {
        String pythonExecutable = System.getenv("PYTHON_EXECUTABLE");
        if (pythonExecutable == null || pythonExecutable.isEmpty()) {
            pythonExecutable = "python3"; // Default to "python3" if not set
        }

        String[] command = {pythonExecutable, SCRIPT_NAME, cutoffDate};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(scriptDir);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            // Capture standard output
            String result = outputReader.lines().collect(Collectors.joining(System.lineSeparator()));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script exited with code: {}", exitCode);
                log.error("Python script output: {}", result);
                throw new IOException("Python script failed: " + result);
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Python script execution interrupted", e);
        } finally {
            // Clean up the temporary directory
            FileSystemUtils.deleteRecursively(scriptDir.toPath());
        }
    }

    private void addReviews(List<Review> reviews) {
        reviews.stream()
                .filter(reviewService::reviewDoesNotExist)
                .map(ReviewMapper::toDto)
                .forEach(reviewService::addOrUpdateReview);
    }
}