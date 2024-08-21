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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            File scriptFile = getScriptFile();
            String result = runPythonScript(scriptFile, cutoffDate);

            List<Review> reviews = JsonUtils.getData(result, new TypeToken<List<Review>>() {});
            assert reviews != null;
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

    private File getScriptFile() throws IllegalArgumentException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + RESOURCE_DIRECTORY);
        }
        Path path = Paths.get(resource.toURI());
        return new File(path.toFile(), SCRIPT_NAME);
    }

    private String runPythonScript(File scriptFile, String cutoffDate) throws IOException, InterruptedException {
        String[] command = {"python3", scriptFile.getAbsolutePath(), cutoffDate};
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String result = reader.lines().collect(Collectors.joining(System.lineSeparator()));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Python script exited with code: {}", exitCode);
                throw new IOException("Python script exited with code: " + exitCode);
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Python script execution interrupted", e);
        }
    }

    private void addReviews(List<Review> reviews) {
        reviews.stream()
                .filter(reviewService::reviewDoesNotExist)
                .map(ReviewMapper::toDto)
                .forEach(reviewService::addOrUpdateReview);
    }
}