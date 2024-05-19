package courses.concordia.service;

import courses.concordia.model.Review;

import java.util.List;

public interface AISummaryGenerator {
    String generateSummary(List<Review> reviews, double avgClarityRating, double avgDifficultyRating);
}
