package courses.concordia.controller.api.v2;

import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.CourseService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@Tag(name = "Course Management", description = "APIs for course operations")
@RequestMapping("/api/v2/courses")
@Controller("CourseControllerV2")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get course by ID")
    @Timed(value = "courses.get", description = "Get course by ID")
    @PostMapping("/{id}")
    public Response<?> getCourseById(
            @PathVariable String id,
            @RequestBody ReviewSortingDto sortType,
            @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset

    ) {
        if (!withReviews) {
            return Response.ok().setPayload(courseService.getCourseById(id));
        }

        return Response.ok().setPayload(courseService.getCourseAndReviewsByIdPaginated(id, limit, offset, sortType));
    }
}
