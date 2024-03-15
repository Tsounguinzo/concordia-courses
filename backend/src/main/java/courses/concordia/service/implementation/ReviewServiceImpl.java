package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.service.ReviewService;
import courses.concordia.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    @Override
    public ReviewDto addReview(Review review) {
        List<Review> existingReviews = reviewRepository.findAllByCourseIdAndUserId(review.getCourseId(), review.getUserId());
        Review updatedReview = review;
        if (existingReviews.isEmpty()) {
            updatedReview = reviewRepository.save(review);
            updateCourseRatings(review.getCourseId());
        }
        return ReviewMapper.toDto(updatedReview);
    }

    @Override
    public ReviewDto updateReview(Review review) {
        Review updatedReview = reviewRepository.save(review);
        updateCourseRatings(review.getCourseId());
        return ReviewMapper.toDto(updatedReview);
    }

    @Override
    public void deleteReview(String courseId, String userId) {
        reviewRepository.deleteByCourseIdAndUserId(courseId, userId);
        updateCourseRatings(courseId);
    }

    @Override
    public List<ReviewDto> getUserReviews(String userId) {
        log.info("Retrieving reviews for user with ID {}", userId);
        Query query = new Query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mongoTemplate.find(query, Review.class)
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    public List<Review> findReviewsByCourseId(String courseId) {
        return reviewRepository.findAllByCourseId(courseId);
    }

    public List<Review> findReviewsByUserId(String userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    public List<Review> findReviewsByInstructorName(String instructorName) {
        return reviewRepository.findAllByInstructor(instructorName);
    }

    private void updateCourseRatings(String courseId) {
        List<Review> reviews = reviewRepository.findAllByCourseId(courseId);
        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setAvgRating(avgRating);
            course.setAvgDifficulty(avgDifficulty);
            course.setReviewCount(reviewsCount);
            courseRepository.save(course);
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
