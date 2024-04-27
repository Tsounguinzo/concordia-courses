package courses.concordia.dto.mapper;

import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.model.Review;

public class ReviewMapper {
    public static ReviewDto toDto(Review review) {
        String reviewType = review.getType();
        if (reviewType == null) return new ReviewDto();

        ReviewDto dto = new ReviewDto();
        dto.set_id(review.get_id());
        dto.setType(reviewType);
        dto.setContent(review.getContent());
        dto.setTimestamp(review.getTimestamp());
        dto.setLikes(review.getLikes());
        dto.setUserId(review.getUserId());

        if (reviewType.equals("school")) {
            dto.setSchoolId(review.getSchoolId());
            return dto;
        }

        dto.setDifficulty(review.getDifficulty());
        dto.setCourseId(review.getCourseId());
        dto.setInstructorId(review.getInstructorId());

        if (reviewType.equals("instructor")) {
            dto.setRating(review.getRating());
            dto.setTags(review.getTags());
            return dto;
        }

        // course type
        dto.setExperience(review.getExperience());
        return dto;
    }
}
