package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.dto.model.course.ReviewPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Review;
import courses.concordia.service.InteractionService;
import courses.concordia.service.NotificationService;
import courses.concordia.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;

    @GetMapping
    public Response<?> getReviews(@RequestParam String userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return Response.ok().setPayload(reviews);
    }

    @PostMapping
    public Response<?> addReview(@RequestBody Review review) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        review = review.setUserId(userId);
        ReviewDto addedReview = reviewService.addReview(review);
        notificationService.addNotifications(review);
        return Response.ok().setPayload(addedReview);
    }

    @PutMapping
    public Response<?> updateReview(@RequestBody Review review) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        review = review.setUserId(userId);
        ReviewDto addedReview = reviewService.updateReview(review);
        notificationService.updateNotifications(userId, review.getCourseId(), review);
        return Response.ok().setPayload(addedReview);
    }

    @DeleteMapping
    public Response<?> deleteReview(@RequestBody ReviewPayloadDto reviewPayloadDto) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        reviewService.deleteReview(reviewPayloadDto.getCourseId(), userId);
        interactionService.deleteInteractions(reviewPayloadDto.getCourseId(), userId);
        notificationService.deleteNotification(userId, reviewPayloadDto.getCourseId());
        return Response.ok().setPayload("Review was deleted successfully");
    }
}
