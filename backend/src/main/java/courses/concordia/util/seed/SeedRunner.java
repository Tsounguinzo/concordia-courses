package courses.concordia.util.seed;

import courses.concordia.util.JsonUtil;
import courses.concordia.util.seed.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SeedRunner {

    private static final Map<String, String> TERM_CODE_MAPPING = new HashMap<>();
    private static final Map<String, String> CODE_TERM_MAPPING = new HashMap<>();

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
        
        List<CourseCatalogue> courseCatalogues = getCourseCatalogues();
        List<CourseWithDetails> courseWithDetails = getCourseWithDetails();
        List<CourseWithTermsAndInstructors> courseWithTermsAndInstructors = getCourseWithTermsAndInstructors();
        List<CourseWithDescription> courseWithDescriptions = getCourseWithDescriptions();
        
        List<Course> newCourses = new ArrayList<>();

        if (courseCatalogues == null || courseCatalogues.isEmpty()) {
            log.error("Course catalogues list is null or empty.");
            return;
        }

        for (CourseCatalogue course : courseCatalogues) {

            var Terms = courseWithTermsAndInstructors.stream().filter(t -> t.getSubject().equals(course.getSubject()) && t.getCatalog().equals(course.getCatalog())).findFirst().orElse(null);
            var Description = courseWithDescriptions.stream().filter(t -> t.getID().equals(course.getID())).findFirst().orElse(null);

            List<String> terms;
            if (Terms == null) {
                System.out.println("terms was null for " + course.getSubject() + " " + course.getCatalog() + " using empty list");
                terms = new ArrayList<>();
            } else {
                terms = Terms.getTerms();
            }


            var coursesBlock = courseWithDetails.stream().filter(t -> t.getSubject().equals(course.getSubject()) && t.getCatalog().equals(course.getCatalog())).toList();
            var schedules = new ArrayList<Course.Schedule>();

            if (terms != null) {
                for (String term : terms) {
                    var blocks = new ArrayList<Course.Block>();
                    for (CourseWithDetails blockDetails : coursesBlock) {
                        if (blockDetails.getTermCode().equals(CODE_TERM_MAPPING.get(term)) || (term.equals("Summer") && blockDetails.getTermCode().equals("2236"))) {
                            blocks.add(new Course.Block(
                                    blockDetails.getComponentCode(),
                                    blockDetails.getLocationCode(),
                                    blockDetails.getRoomCode(),
                                    blockDetails.getSection(),
                                    blockDetails.getClassAssociation(),
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
                System.out.println("description was null for " + course.getID());
                description = "";
            } else {
                description = Description.getDescription();
            }

            var newCourse = new Course(
                    course.getSubject()+course.getCatalog(),
                    terms,
                    List.of(),
                    course.getPrerequisites(),
                    course.getSubject(),
                    description,
                    course.getCatalog(),
                    course.getTitle(),
                    schedules
            );
            newCourses.add(newCourse);

        }

        JsonUtil.toJson(newCourses, "2023-2024-courses.json");
    }

    private static List<CourseWithDescription> getCourseWithDescriptions() {
        return APICallUtil.fetchCourseDescription();
    }

    private static List<CourseWithTermsAndInstructors> getCourseWithTermsAndInstructors() {
        return null;
    }

    private static List<CourseWithDetails> getCourseWithDetails() {
        return null;
    }

    private static List<CourseCatalogue> getCourseCatalogues() {
        return null;
    }


}
