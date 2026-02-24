package courses.concordia.controller.api.v1;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.dto.response.Response;
import courses.concordia.dto.model.course.CourseFilterDto;
import courses.concordia.service.CourseService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    @Value("${beaudelaire.uploadKey}")
    private String uploadKey;

    @Timed(value = "courses.upload", description = "Upload courses file")
    @PutMapping("/update")
    public ResponseEntity<String> updateCourses(@RequestParam("file") MultipartFile file, @RequestParam String key) {
        try {
            if (!key.equals(uploadKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
            }
            courseService.updateCourses(file);
            return ResponseEntity.ok("Courses processed successfully");
        } catch (Exception e) {
            log.error("Error processing courses file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing courses file");
        }
    }

    @Timed(value = "courses.stats", description = "Update courses stats")
    @GetMapping("/stats")
    public ResponseEntity<String> updateCoursesStats(@RequestParam String key) {
        if (!key.equals(uploadKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
        }
        courseService.updateCoursesStatistics();
        return ResponseEntity.ok("Courses stats updated successfully");
    }

    @Timed(value = "home.stats.get", description = "Get homepage stats")
    @GetMapping("/home/stats")
    public Response<?> getHomeStats() {
        return Response.ok().setPayload(courseService.getHomeStats());
    }

    @Timed(value = "courses.get", description = "Get course by ID")
    @PostMapping("/{id}")
    public Response<?> getCourseByIdWithReviews(
            @PathVariable String id,
            @RequestBody ReviewSortingDto sortType,
            @RequestParam(name = "userId", defaultValue = "null") String userId,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        return Response.ok().setPayload(courseService.getCourseAndReviewsByIdPaginated(id, limit, offset, userId, sortType));
    }

    @Timed(value = "courses.get", description = "Get instructors for course")
    @GetMapping("/{id}/instructors")
    public Response<?> getInstructorForCourse(@PathVariable String id) {
        return Response.ok().setPayload(courseService.getInstructors(id));
    }

    @Timed(value = "courses.get", description = "Get courses with filter")
    @PostMapping("/filter")
    public Response<?> getCoursesWithFilters(
            @RequestBody CourseFilterDto filters,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        List<CourseDto> courses = courseService.getCoursesWithFilter(limit, offset, filters);
        return Response.ok().setPayload(courses);
    }
}
