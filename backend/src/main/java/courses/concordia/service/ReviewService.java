package courses.concordia.service;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addOrUpdateReview(ReviewDto reviewDto);
    void deleteReview(String id);
    List<ReviewDto> getUserReviews(String userId);
    List<ReviewDto> getReviewsWithFilter(int limit, int offset, ReviewFilterDto filters);
    ReviewDto getReviewById(String id);
}
