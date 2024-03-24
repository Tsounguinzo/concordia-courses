package courses.concordia.util.seed;

import com.google.gson.reflect.TypeToken;
import courses.concordia.util.JsonUtils;
import courses.concordia.util.seed.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static courses.concordia.util.AIUtils.generateText;
import static courses.concordia.util.seed.ConcordiaAPICallUtil.*;

@Slf4j
public class SeedRunner {

    private static final Map<String, String> TERM_CODE_MAPPING = new HashMap<>();
    private static final Map<String, String> CODE_TERM_MAPPING = new HashMap<>();
    private static final String SEED_FILENAME = "2023-2024-courses-v3.json";

    static {
        TERM_CODE_MAPPING.put("2231", "Summer");
        TERM_CODE_MAPPING.put("2232", "Fall");
        TERM_CODE_MAPPING.put("2233", "Fall/Winter");
        TERM_CODE_MAPPING.put("2234", "Winter");
        TERM_CODE_MAPPING.put("2235", "Spring");
        TERM_CODE_MAPPING.put("2236", "Summer");
        CODE_TERM_MAPPING.put("Summer", "2231");
        CODE_TERM_MAPPING.put("Fall", "2232");
        CODE_TERM_MAPPING.put("Fall/Winter", "2233");
        CODE_TERM_MAPPING.put("Winter", "2234");
        CODE_TERM_MAPPING.put("Spring", "2235");
        // CODE_TERM_MAPPING.put("Summer", "2236");
    }

    public static void main(String[] args) {
        log.info("Starting the seed runner process...");

        long startTime = System.currentTimeMillis();

        List<CourseCatalogue> courseCatalogues = getCourseCatalogues();
        List<CourseWithDescription> courseWithDescriptions = getCourseWithDescriptions();
        List<CourseWithDetails> courseWithDetails = getCourseWithDetails();
        List<CourseWithTermsAndInstructors> courseWithTermsAndInstructors = getCourseWithTermsAndInstructors(courseWithDetails);
        int coursesNotOffered = 0;

        List<Course> newCourses = new ArrayList<>();

        if (courseCatalogues == null || courseCatalogues.isEmpty()) {
            log.error("Course catalogues list is null or empty. Terminating process.");
            return;
        } else {
            log.info("Found {} course catalogues.", courseCatalogues.size());
        }

        for (CourseCatalogue course : courseCatalogues) {

            var Terms = courseWithTermsAndInstructors.stream().filter(t -> t.getSubject().equals(course.getSubject()) && t.getCatalog().equals(course.getCatalog())).findFirst().orElse(null);
            var Description = courseWithDescriptions.stream().filter(t -> t.getID().equals(course.getID())).findFirst().orElse(null);

            List<String> terms;
            if (Terms == null) {
                //log.warn("Terms were null for {} {}. Using empty list.", course.getSubject(), course.getCatalog());
                coursesNotOffered++;
                terms = new ArrayList<>();
            } else {
                terms = Terms.getTerms();
            }


            var coursesBlock = courseWithDetails.stream().filter(t -> t.getSubject().equals(course.getSubject()) && t.getCatalog().equals(course.getCatalog())).toList();
            var schedules = new ArrayList<Course.Schedule>();

            if (terms != null) {
                for (String term : terms) {
                    var blocks = new LinkedHashSet<Course.Block>();
                    for (CourseWithDetails blockDetails : coursesBlock) {
                        if (blockDetails.getTermCode().equals(CODE_TERM_MAPPING.get(term)) || (term.equals("Summer") && blockDetails.getTermCode().equals("2236"))) {
                            blocks.add(new Course.Block(
                                    blockDetails.getComponentCode(),
                                    blockDetails.getLocationCode(),
                                    blockDetails.getRoomCode(),
                                    blockDetails.getSection(),
                                    blockDetails.getBuildingCode(),
                                    blockDetails.getInstructionModeCode(),
                                    blockDetails.getInstructionModeDescription(),
                                    blockDetails.getModays(),
                                    blockDetails.getTuesdays(),
                                    blockDetails.getWednesdays(),
                                    blockDetails.getThursdays(),
                                    blockDetails.getFridays(),
                                    blockDetails.getSaturdays(),
                                    blockDetails.getSundays(),
                                    blockDetails.getClassStartTime(),
                                    blockDetails.getClassEndTime()
                            ));
                        }
                    }
                    var schedule = new Course.Schedule(blocks, term);
                    schedules.add(schedule);
                }
            }

            String description;
            if (Description == null) {
                log.warn("Description was null for {} {}. Using empty text.", course.getSubject(), course.getCatalog());
                description = "";
            } else {
                description = Description.getDescription();
            }

            var newCourse = new Course(
                    course.getSubject() + course.getCatalog(),
                    terms,
                    course.getSubject(),
                    description,
                    course.getPrerequisites(),
                    course.getCatalog(),
                    course.getTitle(),
                    schedules
            );
            newCourses.add(newCourse);

        }

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        long hours = timeTaken / 3600000;
        long minutes = (timeTaken % 3600000) / 60000;
        long seconds = (timeTaken % 60000) / 1000;
        long millis = timeTaken % 1000;

        log.info("Finished processing {} courses with {} not being offered and {} being offered for the current academic year. Saving to JSON.", newCourses.size(), coursesNotOffered, newCourses.size() - coursesNotOffered);
        log.info("Time taken: " + hours + " hour(s) " + minutes + " minute(s) " + seconds + " second(s) " + millis + " millisecond(s)");
        JsonUtils.toJson(newCourses, Paths.get("backend", "src", "main", "resources", "seeds", SEED_FILENAME).toString());
    }

    private static List<CourseWithDescription> getCourseWithDescriptions() {
        String urlStr = BASE_URL + COURSE_DESCRIPTION_ENDPOINT + "*";
        try {
            log.info("Fetching course descriptions from URL: {}", urlStr);
            String response = getRequest(urlStr);
            List<CourseWithDescription> courses = JsonUtils.getData(response, new TypeToken<List<CourseWithDescription>>() {
            });
            assert courses != null;
            log.info("Successfully fetched {} course descriptions", courses.size());
            return courses;
        } catch (Exception e) {
            log.error("Failed to fetch course descriptions from URL: {}, Error: {}", urlStr, e.getMessage(), e);
        }
        return null;
    }

    private static List<CourseWithTermsAndInstructors> getCourseWithTermsAndInstructors(List<CourseWithDetails> courseWithDetails) {
        log.info("Composing courses with terms and instructors");
        HashMap<String, List<String>> courseTerms = new HashMap<>();
        for (CourseWithDetails course : courseWithDetails) {
            String key = course.getSubject() + " " + course.getCatalog();
            String term = TERM_CODE_MAPPING.get(course.getTermCode());
            courseTerms.computeIfAbsent(key, k -> new ArrayList<>()).add(term);
        }

        List<CourseWithTermsAndInstructors> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : courseTerms.entrySet()) {
            String key = entry.getKey();
            List<String> terms = entry.getValue();

            String[] subjectAndCatalog = key.split(" ", 2);
            String subject = subjectAndCatalog[0];
            String catalog = subjectAndCatalog[1];

            CourseWithTermsAndInstructors course = new CourseWithTermsAndInstructors();
            course.setSubject(subject);
            course.setCatalog(catalog);
            course.setTerms(new ArrayList<>(new HashSet<>(terms))); // Remove duplicate terms
            course.setInstructors(new ArrayList<>()); // Placeholder for instructors

            result.add(course);
        }

        log.info("Successfully finished composing the terms of {} course", result.size());
        return result;
    }

    private static List<CourseWithDetails> getCourseWithDetails() {
        Set<String> termCodes = TERM_CODE_MAPPING.keySet();
        List<CourseWithDetails> courses = new ArrayList<>();

        for (String termCode : termCodes) {
            String urlStr = BASE_URL + COURSE_SCHEDULE_TERM_ENDPOINT + "*/" + termCode;
            try {
                log.info("Fetching course with details for {} from URL: {}", TERM_CODE_MAPPING.get(termCode), urlStr);
                String response = ConcordiaAPICallUtil.getRequest(urlStr);
                courses.addAll(Objects.requireNonNull(JsonUtils.getData(response, new TypeToken<List<CourseWithDetails>>() {
                })));
            } catch (Exception e) {
                log.error("Failed to fetch course with details from URL: {}, Error: {}", urlStr, e.getMessage(), e);
            }
        }

        log.info("Successfully fetched {} course with details", courses.size());
        return courses;
    }

    private static List<CourseCatalogue> getCourseCatalogues() {
        String urlStr = BASE_URL + COURSE_CATALOG_ENDPOINT + "*/*/*";
        try {
            log.info("Fetching course catalogues from URL: {}", urlStr);
            String response = ConcordiaAPICallUtil.getRequest(urlStr);
            List<CourseCatalogue> courses = JsonUtils.getData(response, new TypeToken<List<CourseCatalogue>>() {
            });
            assert courses != null;
            log.info("Successfully fetched {} course catalogues", courses.size());
            return courses;
        } catch (Exception e) {
            log.error("Failed to fetch course catalogues from URL: {}, Error: {}", urlStr, e.getMessage(), e);
        }

        return null;
    }


}
