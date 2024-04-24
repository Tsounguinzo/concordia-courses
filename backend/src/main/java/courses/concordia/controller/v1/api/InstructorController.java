package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorService instructorService;
    
    @GetMapping
    public Response<?> getInstructors() {
        return Response
                .ok()
                .setPayload(instructorService.getInstructors());
    }

    @GetMapping("/{name}")
    public Response<?> getInstructorByName(@PathVariable String name, @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews) {
        Object instructor = withReviews ? instructorService.getInstructorAndReviewsByName(name) : instructorService.getInstructorByName(name);
        return Response.ok().setPayload(instructor);
    }

    @PostMapping("/filter")
    public Response<?> getInstructorsWithFilters(@RequestBody InstructorFilterDto filters, @RequestParam int limit, @RequestParam int offset) {
        List<InstructorDto> instructors = instructorService.getInstructorsWithFilter(limit, offset, filters);
        return Response.ok().setPayload(instructors);
    }
}
