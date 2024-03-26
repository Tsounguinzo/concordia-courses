package courses.concordia.controller.v1.api;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.dto.model.course.ReviewPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Review;
import courses.concordia.service.InteractionService;
import courses.concordia.service.NotificationService;
import courses.concordia.service.ReviewService;
import courses.concordia.service.UserService;
import courses.concordia.service.implementation.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;
    private final JwtServiceImpl jwtService;
    private final JwtConfigProperties jwtConfigProperties;

    @GetMapping
    public Response<?> getReviews(@RequestParam String userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return Response.ok().setPayload(reviews);
    }

    @PostMapping
    public Response<?> addReview(@RequestBody Review review, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        review = review.setUserId(userId);
        ReviewDto addedReview = reviewService.addReview(review);
        notificationService.addNotifications(review);
        return Response.ok().setPayload(addedReview);
    }

    @PutMapping
    public Response<?> updateReview(@RequestBody Review review, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        review = review.setUserId(userId);
        ReviewDto addedReview = reviewService.updateReview(review);
        notificationService.updateNotifications(userId, review.getCourseId(), review);
        return Response.ok().setPayload(addedReview);
    }

    @DeleteMapping
    public Response<?> deleteReview(@RequestBody ReviewPayloadDto reviewPayloadDto, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        reviewService.deleteReview(reviewPayloadDto.getCourseId(), userId);
        interactionService.deleteInteractions(reviewPayloadDto.getCourseId(), userId);
        notificationService.deleteNotification(userId, null, reviewPayloadDto.getCourseId());
        return Response.ok().setPayload("Review was deleted successfully");
    }

    private String getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromCookie(request, jwtConfigProperties.getTokenName());
        if (token == null) {
            return null;
        }
        String username = jwtService.extractUsername(token, TokenType.accessToken);
        return userService.getUserIdFromUsername(username);
    }
}
