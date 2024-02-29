package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.model.Review;

public class ReviewMapper {
    public static ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.set_id(review.get_id());
        dto.setContent(review.getContent());
        dto.setDifficulty(review.getDifficulty());
        dto.setRating(review.getRating());
        dto.setLikes(review.getLikes());
        dto.setInstructor(review.getInstructor());
        dto.setTimestamp(review.getTimestamp());
        dto.setCourseId(review.getCourseId());
        dto.setUserId(review.getUserId());
        return dto;
    }
}
