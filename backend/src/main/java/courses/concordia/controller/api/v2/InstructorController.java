package courses.concordia.controller.api.v2;

import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.InstructorService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "Instructor Management", description = "APIs for instructor operations")
@Controller("InstructorControllerV2")
@RequestMapping("/api/v2/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    @Operation(summary = "Get instructor by ID")
    @Timed(value = "instructors.get", description = "Get instructor by ID")
    @PostMapping("/{id}")
    public Response<?> getInstructorById(
            @PathVariable String id,
            @RequestBody ReviewSortingDto sortType,
            @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        if (!withReviews) {
            return Response.ok().setPayload(instructorService.getInstructorById(id));
        }

        return Response.ok().setPayload(instructorService.getInstructorAndReviewsByIdPaginated(id, limit, offset, sortType));
    }

}
