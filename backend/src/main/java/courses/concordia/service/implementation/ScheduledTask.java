package courses.concordia.service.implementation;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Course;
import courses.concordia.repository.CourseRepository;
import courses.concordia.util.JsonUtils;
import courses.concordia.util.seed.model.CourseWithDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static courses.concordia.util.seed.ConcordiaAPICallUtil.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class ScheduledTask {
    private static final Map<String, String> TERM_CODE_MAPPING = new HashMap<>();
    private static final Map<String, String> CODE_TERM_MAPPING = new HashMap<>();
    private static final String ADMIN_EMAIL = "Beaudelaire@tutamail.com";
    private static final String ADMIN_NAME = "Admin";

    private final CourseRepository courseRepository;
    private final EmailServiceImpl emailService;

    static {
        TERM_CODE_MAPPING.put("2231", "Summer 2024");
        TERM_CODE_MAPPING.put("2241", "Summer 2025");
        TERM_CODE_MAPPING.put("2242", "Fall 2024");
        TERM_CODE_MAPPING.put("2243", "Fall/Winter 2024-2025");
        TERM_CODE_MAPPING.put("2244", "Winter 2025");
        TERM_CODE_MAPPING.put("2245", "Spring 2025");
        TERM_CODE_MAPPING.put("2246", "Summer 2025");

        // Initialize reverse mapping
        for (Map.Entry<String, String> entry : TERM_CODE_MAPPING.entrySet()) {
            CODE_TERM_MAPPING.put(entry.getValue(), entry.getKey());
        }
    }

    @Scheduled(cron = "0 0 2 * * ?", zone = "America/New_York")// Run daily at midnight
    public void updateCourseEnrollmentData() {
        log.info("Starting scheduled enrollment data update...");
        long startTime = System.currentTimeMillis();
        String startTimeStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Stats tracking
        int updatedCourses = 0;
        int totalSchedules = 0;
        Map<String, Integer> retentionReasons = new HashMap<>();
        retentionReasons.put("Term not found", 0);
        retentionReasons.put("No term data", 0);
        retentionReasons.put("No blocks found", 0);

        try {
            List<Course> dbCourses = courseRepository.findAll();
            Map<String, List<CourseWithDetails>> latestDataByTerm = fetchLatestEnrollmentData();

            if (latestDataByTerm.isEmpty()) {
                emailService.sendFailureMailMessage(
                        ADMIN_NAME,
                        ADMIN_EMAIL,
                        "Failed to fetch enrollment data from Concordia API"
                );
                return;
            }

            for (Course course : dbCourses) {
                boolean courseUpdated = false;
                List<Course.Schedule> updatedSchedules = new ArrayList<>();

                for (Course.Schedule oldSchedule : course.getSchedules()) {
                    totalSchedules++;
                    String termCode = CODE_TERM_MAPPING.get(oldSchedule.getTerm());

                    if (termCode == null) {
                        updatedSchedules.add(oldSchedule);
                        retentionReasons.merge("Term not found", 1, Integer::sum);
                        log.warn("Term not found for term: {} {}", oldSchedule.getTerm(), oldSchedule);
                        continue;
                    }

                    List<CourseWithDetails> termData = latestDataByTerm.get(termCode);
                    if (termData == null) {
                        updatedSchedules.add(oldSchedule);
                        retentionReasons.merge("No term data", 1, Integer::sum);
                        continue;
                    }

                    List<CourseWithDetails> courseBlocks = termData.stream()
                            .filter(block ->
                                    block.getSubject().equals(course.getSubject()) &&
                                            block.getCatalog().equals(course.getCatalog()))
                            .toList();

                    if (!courseBlocks.isEmpty()) {
                        Set<Course.Block> newBlocks = new LinkedHashSet<>();
                        for (CourseWithDetails blockData : courseBlocks) {
                            newBlocks.add(new Course.Block(
                                    blockData.getComponentCode(),
                                    blockData.getLocationCode(),
                                    blockData.getRoomCode(),
                                    blockData.getSection(),
                                    blockData.getSession(),
                                    blockData.getBuildingCode(),
                                    blockData.getInstructionModeCode(),
                                    blockData.getInstructionModeDescription(),
                                    blockData.getModays(),
                                    blockData.getTuesdays(),
                                    blockData.getWednesdays(),
                                    blockData.getThursdays(),
                                    blockData.getFridays(),
                                    blockData.getSaturdays(),
                                    blockData.getSundays(),
                                    blockData.getClassStartTime(),
                                    blockData.getClassEndTime(),
                                    blockData.getEnrollmentCapacity(),
                                    blockData.getCurrentEnrollment(),
                                    blockData.getWaitlistCapacity(),
                                    blockData.getCurrentWaitlistTotal(),
                                    blockData.getHasSeatReserved()
                            ));
                        }

                        Course.Schedule newSchedule = new Course.Schedule(newBlocks.stream().toList(), oldSchedule.getTerm());
                        updatedSchedules.add(newSchedule);
                        courseUpdated = true;
                    } else {
                        updatedSchedules.add(oldSchedule);
                        retentionReasons.merge("No blocks found", 1, Integer::sum);
                        log.warn("No blocks found for course: {} {}", course.getSubject(), course.getCatalog());
                    }
                }

                if (courseUpdated) {
                    course.setSchedules(updatedSchedules);
                    courseRepository.save(course);
                    updatedCourses++;
                }
            }

            long duration = System.currentTimeMillis() - startTime;
            String endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            emailService.sendEnrollmentUpdateReport(
                    ADMIN_NAME,
                    ADMIN_EMAIL,
                    startTimeStr,
                    endTimeStr,
                    duration,
                    dbCourses.size(),
                    totalSchedules,
                    updatedCourses,
                    retentionReasons
            );

        } catch (Exception e) {
            emailService.sendFailureMailMessage(
                    ADMIN_NAME,
                    ADMIN_EMAIL,
                    "Error updating enrollment data: " + e.getMessage()
            );
            log.error("Error updating enrollment data: ", e);
        }
    }

    private Map<String, List<CourseWithDetails>> fetchLatestEnrollmentData() {
        Map<String, List<CourseWithDetails>> coursesByTerm = new HashMap<>();

        for (String termCode : TERM_CODE_MAPPING.keySet()) {
            String url = BASE_URL + COURSE_SCHEDULE_TERM_ENDPOINT + "*/" + termCode;
            try {
                log.debug("Fetching enrollment data for term {}", TERM_CODE_MAPPING.get(termCode));
                String response = getRequest(url);
                List<CourseWithDetails> termCourses = JsonUtils.getData(response,
                        new TypeToken<List<CourseWithDetails>>() {});

                if (termCourses != null && !termCourses.isEmpty()) {
                    coursesByTerm.put(termCode, termCourses);
                }
            } catch (Exception e) {
                log.error("Failed to fetch data for term {}: {}", termCode, e.getMessage());
            }
        }

        return coursesByTerm;
    }
}