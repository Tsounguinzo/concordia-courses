package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.response.Response;
import courses.concordia.dto.model.course.CourseFilterDto;
import courses.concordia.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    @GetMapping
    public Response<?> getCourses() {
        return Response
                .ok()
                .setPayload(courseService.getCourses());
    }

    @GetMapping("/{id}")
    public Response<?> getCourseById(@PathVariable String id, @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews) {
        Object course = withReviews ? courseService.getCourseAndReviewsById(id) : courseService.getCourseById(id);
        return Response.ok().setPayload(course);
    }

    @PostMapping("/filter")
    public Response<?> getCourses(@RequestBody CourseFilterDto filters, @RequestParam int limit, @RequestParam int offset) {
        List<CourseDto> courses = courseService.getCoursesWithFilter(limit, offset, filters);
        return Response.ok().setPayload(courses);
    }
}
