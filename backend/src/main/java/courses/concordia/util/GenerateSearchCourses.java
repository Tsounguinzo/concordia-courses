package courses.concordia.util;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Instructor;
import courses.concordia.util.seed.model.Course;
import org.thymeleaf.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenerateSearchCourses {
    public static final Path COURSE_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "db.courses.json");
    public static final Path INSTRUCTOR_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "db.instructors.json");
    public static final Path SEARCH_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "searchCourses.json");
    public static final Path COURSE_CODES_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "courseCodes.json");
    public static final Path INSTRUCTORS_LIST_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "instructorsList.json");

    public static void main(String[] args) throws Exception {
        List<Course> courses = JsonUtils.getData(COURSE_FILE_PATH, new TypeToken<List<Course>>() {});
        List<Instructor> instructors = JsonUtils.getData(INSTRUCTOR_FILE_PATH, new TypeToken<List<Instructor>>() {});

        // Map instructors to their courses
        Map<String, List<String>> courseInstructors = instructors.stream()
                .flatMap(instructor -> {
                    String instructorName = instructor.getFirstName() + " " + instructor.getLastName();
                    // If instructor has no courses, return empty stream
                    if (instructor.getCourses() == null || instructor.getCourses().isEmpty()) {
                        return Stream.empty();
                    }
                    // Otherwise map each course to this instructor
                    return instructor.getCourses().stream()
                            .map(course -> new AbstractMap.SimpleEntry<>(
                                    (course.getSubject() + course.getCatalog()).toUpperCase(),
                                    instructorName));
                })
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        // Transform courses to desired output format
        List<Map<String, Object>> result = courses.stream().map(course -> {
            Map<String, Object> courseMap = new LinkedHashMap<>();
            courseMap.put("_id", course.get_id());
            courseMap.put("title", course.getTitle());
            courseMap.put("subject", course.getSubject());
            courseMap.put("catalog", course.getCatalog());
            courseMap.put("instructors",
                    courseInstructors.getOrDefault(course.get_id(), Collections.emptyList())
                            .stream()
                            .map(String::toLowerCase)
                            .map(instructor -> Arrays.stream(instructor.split(" "))
                                    .map(StringUtils::capitalize)
                                    .collect(Collectors.joining(" "))
                            )
                            .collect(Collectors.toList())
            );
            return courseMap;
        }).collect(Collectors.toList());

        // Create a separate list of all instructors to ensure those without courses are included
        List<String> allInstructors = instructors.stream()
                .map(instructor -> {
                    String name = instructor.getFirstName() + " " + instructor.getLastName();
                    return name.toLowerCase().trim();
                })
                .map(instructor -> Arrays.stream(instructor.split(" "))
                        .map(StringUtils::capitalize)
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.toList());

        // Save all instructors to a separate file
        JsonUtils.toJson(allInstructors, INSTRUCTORS_LIST_FILE_PATH.toString());

        // All courses subject
        Set<String> courseSubjects = courses.stream().map(Course::getSubject).collect(Collectors.toSet());

        JsonUtils.toJson(result, SEARCH_FILE_PATH.toString());
        JsonUtils.toJson(courseSubjects, COURSE_CODES_FILE_PATH.toString());
    }
}