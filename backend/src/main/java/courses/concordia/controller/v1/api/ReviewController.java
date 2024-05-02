package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;
import courses.concordia.dto.model.review.ReviewPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.User;
import courses.concordia.service.InteractionService;
import courses.concordia.service.NotificationService;
import courses.concordia.service.ReviewService;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;

    @GetMapping
    public Response<?> getReviews(@RequestParam String userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return Response.ok().setPayload(reviews);
    }

    @GetMapping("/shared")
    public Response<?> getReview(@RequestParam String id) {
        ReviewDto review = reviewService.getReviewById(id);
        return Response.ok().setPayload(review);
    }

    @PostMapping("/filter")
    public Response<?> getReviewsWithFilters(@RequestBody ReviewFilterDto filters, @RequestParam int limit, @RequestParam int offset) {
        List<ReviewDto> reviews = reviewService.getReviewsWithFilter(limit, offset, filters);
        return Response.ok().setPayload(reviews);
    }

    @PostMapping
    public Response<?> addReview(@RequestBody ReviewDto reviewDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        reviewDto = reviewDto.setUserId(user.get_id());
        ReviewDto addedReview = reviewService.addOrUpdateReview(reviewDto);
        notificationService.addNotifications(reviewDto);
        return Response.ok().setPayload(addedReview);
    }

    @PutMapping
    public Response<?> updateReview(@RequestBody ReviewDto reviewDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        reviewDto = reviewDto.setUserId(user.get_id());
        ReviewDto addedReview = reviewService.addOrUpdateReview(reviewDto);
        notificationService.updateNotifications(user.get_id(), reviewDto.getCourseId(), reviewDto);
        return Response.ok().setPayload(addedReview);
    }

    @DeleteMapping
    public Response<?> deleteReview(@RequestBody ReviewPayloadDto reviewPayloadDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        ReviewDto reviewDto = reviewService.getReviewById(reviewPayloadDto.getId());
        reviewService.deleteReview(reviewPayloadDto.getId());
        interactionService.deleteInteractions(reviewPayloadDto.getId(), user.get_id());
        notificationService.deleteNotification(user.get_id(), null, reviewDto.getCourseId());
        return Response.ok().setPayload("Review was deleted successfully");
    }
}