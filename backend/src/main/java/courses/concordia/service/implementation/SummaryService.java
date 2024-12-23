package courses.concordia.service.implementation;

import courses.concordia.model.Instructor;
import courses.concordia.model.Review;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.repository.ReviewRepository;
import courses.concordia.service.AISummaryGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static courses.concordia.util.DateUtils.getLocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {

    private final InstructorRepository instructorRepository;
    private final AISummaryGenerator aiSummaryGenerator;
    private final ReviewRepository reviewRepository;
    private static final int REVIEW_THRESHOLD = 10;
    private static final int REVIEW_CEILING = 450;


    @Scheduled(cron = "0 0 2 * * ?", zone = "America/New_York")// Run daily at 2 am
    public void updateSummaries() {
        log.info("Starting to update summaries for instructors");
        List<Instructor> instructors = instructorRepository.findAll();
        for (Instructor instructor : instructors) {
            if (instructor.getReviewCount() >= REVIEW_THRESHOLD && instructor.getReviewCount() != instructor.getLastReviewCount()) {
                List<Review> instructorReviews = reviewRepository.findAllByInstructorIdAndType(instructor.get_id(), "instructor");
                if (instructorReviews.size() > REVIEW_CEILING) {
                    instructorReviews = instructorReviews.subList(0, REVIEW_CEILING);
                }
                String summary = aiSummaryGenerator.generateSummary(instructorReviews, instructor.getAvgRating(), instructor.getAvgDifficulty());
                if (summary == null) continue;
                instructor.setAiSummary(summary);
                instructor.setLastReviewCount(instructor.getReviewCount());
                instructor.setLastSummaryUpdate(getLocalDateTime());
                instructorRepository.save(instructor);
                log.info("Updated summary for instructor {}", instructor.get_id());
            }
        }
        log.info("Finished updating summaries for instructors");
    }
}

