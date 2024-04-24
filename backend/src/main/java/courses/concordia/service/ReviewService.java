package courses.concordia.service;
import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addOrUpdateReview(ReviewDto reviewDto);
    void deleteReview(String courseId, String userId);
    List<ReviewDto> getUserReviews(String userId);
    List<ReviewDto> getReviewsWithFilter(int limit, int offset, ReviewFilterDto filters);
}
