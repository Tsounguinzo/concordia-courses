package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Review;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.COURSE;
import static courses.concordia.exception.EntityType.REVIEW;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    @Override
    public ReviewDto addReview(Review review) {
        Review updatedReview = reviewRepository.save(review);
        return ReviewMapper.toDto(updatedReview);
    }

    @Override
    public ReviewDto updateReview(Review review) {
        Review updatedReview = reviewRepository.save(review);
        return ReviewMapper.toDto(updatedReview);
    }

    @Override
    public void deleteReview(String reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isPresent()) {
            reviewRepository.delete(review.get());
        } else {
            throw exception(REVIEW, ENTITY_NOT_FOUND, reviewId);
        }
    }

    @Override
    public List<ReviewDto> getUserReviews(String userId) {
        log.info("Retrieving reviews for user with ID {}", userId);
        Query query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
