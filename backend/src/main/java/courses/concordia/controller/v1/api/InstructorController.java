package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.response.ProcessingResult;
import courses.concordia.dto.response.Response;
import courses.concordia.service.InstructorService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorService instructorService;
    @Value("${beaudelaire.uploadKey}")
    private String uploadKey;

    @Timed(value = "instructors.upload", description = "Upload instructors file")
    @PutMapping("/upload")
    public ResponseEntity<String> uploadInstructors(@RequestParam("file") MultipartFile file, @RequestParam String key) {
        if (!key.equals(uploadKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
        }

        try {
            ProcessingResult result = instructorService.uploadInstructors(file);
            // Construct a summary response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Instructors processed with some results");
            responseBody.put("added", result.getAddedCount());
            responseBody.put("updated", result.getUpdatedCount());
            responseBody.put("alreadyExisted", result.getAlreadyExistsCount());
            responseBody.put("failed", result.getFailedCount());
            if (!result.getErrors().isEmpty()) {
                responseBody.put("errors", result.getErrors());
            }
            return ResponseEntity.ok(responseBody.toString());

        } catch (Exception e) {
            log.error("Error processing instructors file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing instructors file");
        }
    }

    @Timed(value = "instructors.get", description = "Get instructors")
    @GetMapping
    public Response<?> getInstructors() {
        return Response
                .ok()
                .setPayload(instructorService.getInstructors());
    }

    @Timed(value = "instructors.stats", description = "Update instructors stats")
    @GetMapping("/stats")
    public ResponseEntity<String> updateInstructorsStats(@RequestParam String key) {
        if (!key.equals(uploadKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
        }
        instructorService.updateInstructorsStatistics();
        return ResponseEntity.ok("Instructors stats updated successfully");
    }

    @Timed(value = "instructors.get", description = "Get instructor by ID")
    @GetMapping("/{id}")
    public Response<?> getInstructorById(@PathVariable String id, @RequestParam(name = "with_reviews", defaultValue = "false") boolean withReviews) {
        Object instructorPayload = withReviews ? instructorService.getInstructorAndReviewsById(id) : instructorService.getInstructorById(id);
        return Response.ok().setPayload(instructorPayload);
    }

    @Timed(value = "instructors.get", description = "Get instructors with filter")
    @PostMapping("/filter")
    public Response<?> getInstructorsWithFilters(@RequestBody InstructorFilterDto filters, @RequestParam int limit, @RequestParam int offset) {
        List<InstructorDto> instructors = instructorService.getInstructorsWithFilter(limit, offset, filters);
        return Response.ok().setPayload(instructors);
    }
}
