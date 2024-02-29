package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.IdDto;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Review;
import courses.concordia.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public Response getReviews(@RequestParam String userId) {
        List<ReviewDto> reviews = reviewService.getUserReviews(userId);
        return Response.ok().setPayload(reviews);
    }

    @PostMapping
    public Response addReview(@RequestBody Review review) {
        ReviewDto addedReview = reviewService.addReview(review);
        return Response.ok().setPayload(addedReview);
    }

    @PutMapping
    public Response updateReview(@RequestBody Review review) {
        ReviewDto addedReview = reviewService.updateReview(review);
        return Response.ok().setPayload(addedReview);
    }

    @DeleteMapping
    public Response deleteReview(@RequestBody IdDto idDto) {
        reviewService.deleteReview(idDto.getId());
        return Response.ok().setPayload("Review was deleted successfully");
    }
}
