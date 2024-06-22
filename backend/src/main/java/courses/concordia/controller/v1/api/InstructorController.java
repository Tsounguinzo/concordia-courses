package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.InstructorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorService instructorService;
    @Value("${beaudelaire.uploadKey}")
    private String uploadKey;

    @PutMapping("/upload")
    public ResponseEntity<String> uploadInstructors(@RequestParam("file") MultipartFile file, @RequestParam String key) {
        try {
            if (!key.equals(uploadKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
            }
            instructorService.uploadInstructors(file);
            return ResponseEntity.ok("Instructors processed successfully");
        } catch (Exception e) {
            log.error("Error processing instructors file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing instructors file");
        }
    }
    @GetMapping
    public Response<?> getInstructors() {
        return Response
                .ok()
                .setPayload(instructorService.getInstructors());
    }

    @GetMapping("/{id}")
    public Response<?> getInstructorById(@PathVariable String id, @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews) {
        Object instructorPayload = withReviews ? instructorService.getInstructorAndReviewsById(id) : instructorService.getInstructorById(id);
        return Response.ok().setPayload(instructorPayload);
    }

    @PostMapping("/filter")
    public Response<?> getInstructorsWithFilters(@RequestBody InstructorFilterDto filters, @RequestParam int limit, @RequestParam int offset) {
        List<InstructorDto> instructors = instructorService.getInstructorsWithFilter(limit, offset, filters);
        return Response.ok().setPayload(instructors);
    }
}
