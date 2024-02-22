package courses.concordia.rest;

import courses.concordia.model.Course;
import courses.concordia.model.CourseFilter;
import courses.concordia.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;
    @GetMapping("/courses")
    public List<Course> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/courses/{id}")
    public Object getCourseById(@PathVariable String id, @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews) {
        if (withReviews) {
            return courseService.getCourseAndReviewsById(id);
        } else {
            return courseService.getCourseById(id);
        }
    }

    @PostMapping("/courses/filter")
    public List<Course> getCourses(@RequestBody CourseFilter filters, @RequestParam int limit, @RequestParam int offset) {
        return courseService.getCourses(limit, offset, filters);
    }
}
