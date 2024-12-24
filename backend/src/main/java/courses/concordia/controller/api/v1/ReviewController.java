package courses.concordia.controller.api.v1;

import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;
import courses.concordia.dto.model.review.ReviewPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.dto.response.ProcessingResult;
import courses.concordia.model.User;
import courses.concordia.service.InteractionService;
import courses.concordia.service.NotificationService;
import courses.concordia.service.ReviewService;
import courses.concordia.service.UserService;
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
@RequestMapping("/api/v1/reviews")
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;
    @Value("${beaudelaire.uploadKey}")
    private String uploadKey;

    @Timed(value = "reviews.upload", description = "Upload reviews file")
    @PutMapping("/upload")
    public ResponseEntity<String> uploadReviews(@RequestParam("file") MultipartFile file, @RequestParam String key) {
            if (!key.equals(uploadKey)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
            }

        try {
            ProcessingResult result = reviewService.uploadReviews(file);
            // Construct a summary response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Reviews processed with some results");
            responseBody.put("added", result.getAddedCount());
            responseBody.put("alreadyExisted", result.getAlreadyExistsCount());
            responseBody.put("failed", result.getFailedCount());
            if (!result.getErrors().isEmpty()) {
                responseBody.put("errors", result.getErrors());
            }
            return ResponseEntity.ok(responseBody.toString());

        } catch (Exception e) {
            log.error("Error processing reviews file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing reviews file");
        }
    }

    @Timed(value = "reviews.get", description = "Get reviews")
    @GetMapping
    public Response<?> getReviews(@RequestParam String userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return Response.ok().setPayload(reviews);
    }

    @Timed(value = "reviews.get", description = "Get review by ID")
    @GetMapping("/shared")
    public Response<?> getReview(@RequestParam String id) {
        ReviewDto review = reviewService.getReviewById(id);
        return Response.ok().setPayload(review);
    }

    @Timed(value = "reviews.get", description = "Get reviews with filters")
    @PostMapping("/filter")
    public Response<?> getReviewsWithFilters(
            @RequestBody ReviewFilterDto filters,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        List<ReviewDto> reviews = reviewService.getReviewsWithFilter(limit, offset, filters);
        return Response.ok().setPayload(reviews);
    }

    @Timed(value = "reviews.get", description = "Get reviews by course ID")
    @PostMapping
    public Response<?> addReview(@RequestBody ReviewDto reviewDto) {
        User user = userService.getAuthenticatedUser();

        if (user == null && reviewDto.getUserId() == null) {
            return Response.unauthorized();
        }

        if (user != null) {
            reviewDto.setUserId(user.get_id());
        }

        ReviewDto addedReview = reviewService.addOrUpdateReview(reviewDto);
        notificationService.addNotifications(reviewDto);
        return Response.ok().setPayload(addedReview);
    }

    @Timed(value = "reviews.update", description = "Update review")
    @PutMapping
    public Response<?> updateReview(@RequestBody ReviewDto reviewDto) {
        ReviewDto addedReview = reviewService.addOrUpdateReview(reviewDto);
        notificationService.updateNotifications(reviewDto.get_id(), reviewDto.getCourseId(), reviewDto);
        return Response.ok().setPayload(addedReview);
    }

    @Timed(value = "reviews.delete", description = "Delete review")
    @DeleteMapping
    public Response<?> deleteReview(@RequestBody ReviewPayloadDto reviewPayloadDto) {
        ReviewDto reviewDto = reviewService.getReviewById(reviewPayloadDto.getId());
        interactionService.deleteInteractions(reviewPayloadDto.getId(), reviewPayloadDto.getUserId(), reviewDto.getType());
        notificationService.deleteNotification(reviewPayloadDto.getUserId(), null, reviewDto.getCourseId());
        reviewService.deleteReview(reviewPayloadDto.getId(), reviewPayloadDto.getType(), reviewPayloadDto.getCourseId(), reviewPayloadDto.getInstructorId());
        return Response.ok().setPayload("Review was deleted successfully");
    }

    @Timed(value = "reviews.delete", description = "Delete reviews with non-existing instructors")
    @DeleteMapping("/instructor")
    public ResponseEntity<String> deleteReviewsWithNonExistingInstructors() {
        ProcessingResult result = reviewService.deleteReviewsWithNonExistentInstructorIds();
        return ResponseEntity.ok("Reviews deleted successfully. " + result.getDeletedCount() + " reviews were deleted.");
    }

    @Timed(value = "reviews.delete", description = "Delete duplicate reviews")
    @DeleteMapping("/duplicate")
    public ResponseEntity<String> deleteDuplicateReviews() {
        ProcessingResult result = reviewService.deleteDuplicateReviews();
        return ResponseEntity.ok("Duplicate reviews deleted successfully. " + result.getDeletedCount() + " reviews were deleted.");
    }
}