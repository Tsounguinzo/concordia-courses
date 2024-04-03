package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.dto.model.course.ReviewPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Review;
import courses.concordia.model.User;
import courses.concordia.service.*;
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
        reviewService.deleteReview(reviewPayloadDto.getCourseId(), user.get_id());
        interactionService.deleteInteractions(reviewPayloadDto.getCourseId(), user.get_id());
        notificationService.deleteNotification(user.get_id(), null, reviewPayloadDto.getCourseId());
        return Response.ok().setPayload("Review was deleted successfully");
    }
}