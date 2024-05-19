package courses.concordia.service.implementation;

import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.service.AISummaryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final InstructorRepository instructorRepository;
    private final AISummaryGenerator aiSummaryGenerator;
    private final ReviewRepository reviewRepository;
    private static final int REVIEW_THRESHOLD = 10;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        updateSummaries();
    }
    @Scheduled(cron = "0 0 0 * * ?")  // Run daily at midnight
    public void updateSummaries() {
        List<Instructor> instructors = instructorRepository.findAll();
        for (Instructor instructor : instructors) {
            if (instructor.getReviewCount() >= REVIEW_THRESHOLD && instructor.getReviewCount() != instructor.getLastReviewCount()) {
                List<Review> instructorReviews = reviewRepository.findAllByInstructorIdAndType(instructor.get_id(), "instructor");
                String summary = aiSummaryGenerator.generateSummary(instructorReviews, instructor.getAvgRating(), instructor.getAvgDifficulty());
                if (summary == null) continue;
                instructor.setAiSummary(summary);
                instructor.setLastReviewCount(instructor.getReviewCount());
                instructor.setLastSummaryUpdate(LocalDateTime.now());
                instructorRepository.save(instructor);
            }
        }
    }
}

