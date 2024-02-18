package courses.concordia.service;
import courses.concordia.model.Review;

import java.util.List;

public interface ReviewService {
    List<Review> getAllReviews();
    Review addReview(Review review);
    Review updateReview(Review review);
    void deleteReview(String id);
    Review getReviewById(String id);
}
