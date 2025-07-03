package courses.concordia.dto.mapper;

import courses.concordia.dto.model.review.ReviewDto;
import courses.concordia.dto.model.CommentDto;
import courses.concordia.dto.model.ResourceLinkDto;
import courses.concordia.model.Review;
import courses.concordia.model.Comment;
import courses.concordia.model.ResourceLink;
import java.util.stream.Collectors;

public class ReviewMapper {
    public static ReviewDto toDto(Review review) {
        String reviewType = review.getType() == null ? "course" : review.getType();

        ReviewDto dto = new ReviewDto();
        dto.set_id(review.get_id());
        dto.setType(reviewType);
        dto.setContent(review.getContent());
        dto.setTimestamp(review.getTimestamp());
        dto.setLikes(review.getLikes());
        dto.setUserId(review.getUserId());
        dto.setComments(review.getComments().stream()
            .map(ReviewMapper::commentToDto)
            .collect(Collectors.toList()));
        dto.setResourceLinks(review.getResourceLinks().stream()
            .map(ReviewMapper::resourceLinkToDto)
            .collect(Collectors.toList()));

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
    
    private static CommentDto commentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.set_id(comment.get_id());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setTimestamp(comment.getTimestamp());
        return dto;
    }
    
    private static ResourceLinkDto resourceLinkToDto(ResourceLink resourceLink) {
        ResourceLinkDto dto = new ResourceLinkDto();
        dto.set_id(resourceLink.get_id());
        dto.setUserId(resourceLink.getUserId());
        dto.setUrl(resourceLink.getUrl());
        dto.setDescription(resourceLink.getDescription());
        dto.setTimestamp(resourceLink.getTimestamp());
        return dto;
    }
}
