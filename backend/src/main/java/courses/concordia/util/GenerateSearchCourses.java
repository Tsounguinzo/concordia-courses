package courses.concordia.util;

import com.google.gson.reflect.TypeToken;
import courses.concordia.model.Instructor;
import courses.concordia.util.seed.model.Course;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateSearchCourses {
    public static final Path COURSE_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "db.courses.json");
    public static final Path INSTRUCTOR_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "db.instructors.json");
    public static final Path SEARCH_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "searchCourses.json");
    public static final Path COURSE_CODES_FILE_PATH = Paths.get("backend", "src", "main", "resources", "data", "courseCodes.json");

    public static void main(String[] args) throws Exception {

        List<Course> courses = JsonUtils.getData(COURSE_FILE_PATH, new TypeToken<List<Course>>() {});
        List<Instructor> instructors = JsonUtils.getData(INSTRUCTOR_FILE_PATH, new TypeToken<List<Instructor>>() {});

        // Map instructors to their courses
        Map<String, List<String>> courseInstructors = instructors.stream()
                .flatMap(instructor -> instructor.getCourses().stream()
                        .map(course -> new AbstractMap.SimpleEntry<>((course.getSubject() + course.getCatalog()).toUpperCase(), instructor.getFirstName() + " " + instructor.getLastName()))
                )
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        // Transform courses to desired output format
        List<Map<String, Object>> result = courses.stream().map(course -> {
            Map<String, Object> courseMap = new LinkedHashMap<>();
            courseMap.put("_id", course.get_id());
            courseMap.put("title", course.getTitle());
            courseMap.put("subject", course.getSubject());
            courseMap.put("catalog", course.getCatalog());
            courseMap.put("instructors", courseInstructors.getOrDefault(course.get_id(), Collections.emptyList()));
            return courseMap;
        }).collect(Collectors.toList());

        // ALl courses subject
        Set<String> courseSubjects = courses.stream().map(Course::getSubject).collect(Collectors.toSet());

        JsonUtils.toJson(result, SEARCH_FILE_PATH.toString());
        JsonUtils.toJson(courseSubjects, COURSE_CODES_FILE_PATH.toString());
    }
}
