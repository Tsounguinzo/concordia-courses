package courses.concordia.service;

import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.review.ReviewFilterDto;
import courses.concordia.dto.response.ProcessingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewDto addOrUpdateReview(ReviewDto reviewDto);
    void deleteReview(String id, String type, String courseId, String instructorId);
    List<ReviewDto> getUserReviews(String userId);
    List<ReviewDto> getReviewsWithFilter(int limit, int offset, ReviewFilterDto filters);
    ReviewDto getReviewById(String id);
    ProcessingResult uploadReviews(MultipartFile file);

    ProcessingResult deleteDuplicateReviews();

    ProcessingResult deleteReviewsWithNonExistentInstructorIds();
}
