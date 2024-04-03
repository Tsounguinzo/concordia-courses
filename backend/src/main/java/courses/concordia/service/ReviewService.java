package courses.concordia.service;
import courses.concordia.dto.model.course.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addOrUpdateReview(ReviewDto reviewDto);
    void deleteReview(String courseId, String userId);
    List<ReviewDto> getUserReviews(String userId);
}
