package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.ReviewMapper;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.model.Review;
import courses.concordia.repository.CourseRepository;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.service.ReviewService;
import courses.concordia.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#reviewDto.courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
    })
    @Override
    public ReviewDto addOrUpdateReview(ReviewDto reviewDto) {
        Review review = reviewRepository
                .findByCourseIdAndUserId(reviewDto.getCourseId(), reviewDto.getUserId())
                .map(r -> updateReviewFromDto(r, reviewDto))
                .orElseGet(() -> createReviewFromDto(reviewDto));

        review = reviewRepository.save(review);
        updateCourseExperience(review.getCourseId());

        return ReviewMapper.toDto(review);
    }

    private Review createReviewFromDto(ReviewDto reviewDto) {
        return modelMapper.map(reviewDto, Review.class);
    }

    private Review updateReviewFromDto(Review existingReview, ReviewDto reviewDto) {
        modelMapper.map(reviewDto, existingReview);
        return existingReview;
    }

    @Caching(evict = {
            @CacheEvict(value = "courseReviewsCache", key = "#courseId"),
            @CacheEvict(value = "coursesCacheWithFilters", allEntries = true)
    })
    @Override
    public void deleteReview(String courseId, String userId) {
        reviewRepository.deleteByCourseIdAndUserId(courseId, userId);
        updateCourseExperience(courseId);
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

    private void updateCourseExperience(String courseId) {
        List<Review> reviews = reviewRepository.findAllByCourseId(courseId);
        double avgExperience = reviews.stream().mapToInt(Review::getExperience).average().orElse(0.0);
        double avgDifficulty = reviews.stream().mapToInt(Review::getDifficulty).average().orElse(0.0);
        int reviewsCount = reviews.size();
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setAvgExperience(avgExperience);
            course.setAvgDifficulty(avgDifficulty);
            course.setReviewCount(reviewsCount);
            courseRepository.save(course);
        }
    }
}
