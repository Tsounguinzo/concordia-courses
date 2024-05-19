package courses.concordia.service.implementation;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.core.credential.KeyCredential;
import courses.concordia.model.Review;
import courses.concordia.service.AISummaryGenerator;
import io.github.resilience4j.core.functions.CheckedSupplier;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AISummaryGeneratorImpl implements AISummaryGenerator {

    @Value("${openai.apiKey}")
    private String apiKey;
    @Value("${openai.modelName}")
    private String modelName;

    private static final RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(500) // Requests per minute (RPM)
            .limitRefreshPeriod(Duration.ofMinutes(1))
            .timeoutDuration(Duration.ofSeconds(5))
            .build();

    private static final RateLimiterRegistry registry = RateLimiterRegistry.of(config);
    private static final RateLimiter rateLimiter = registry.rateLimiter("openai");

    @Override
    public String generateSummary(List<Review> reviews, double avgClarityRating, double avgDifficultyRating) {
        String reviewsText = reviews.stream()
                .map(review -> String.format(
                        "Review: \"%s\" (Clarity: %d/5, Difficulty: %d/5)",
                        review.getContent(),
                        review.getRating(),
                        review.getDifficulty()))
                .collect(Collectors.joining(" "));

        String prompt = "Summarize the following reviews. The summary should include specific details about the following aspects: teaching style, clarity of explanations, engagement level, organization, exam difficulty, and any particular strengths or weaknesses mentioned by students. " +
                "Consider the average clarity rating of " + avgClarityRating + " and the average difficulty rating of " + avgDifficultyRating + ". Make sure that the summary includes specific details and examples from the reviews to support your explanations, avoid general statements, and aim for clear and concise explanations of the students' feedback. Avoid repetition from each of the topics and limit to two sentences each: " + reviewsText;

        try {
            CheckedSupplier<String> summarySupplier = () -> {
                OpenAIClient client = new OpenAIClientBuilder()
                        .credential(new KeyCredential(apiKey))
                        .buildClient();

                ChatRequestMessage chatMessage = new ChatRequestUserMessage(prompt);

                ChatCompletions chatCompletions = client.getChatCompletions(modelName,
                        new ChatCompletionsOptions(List.of(chatMessage)));

                return chatCompletions
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent()
                        .trim();
            };
            return RateLimiter.decorateCheckedSupplier(rateLimiter, summarySupplier).get();

        } catch (Throwable e) {
            log.error("Failed to generate summary for {}", reviews.get(0).getInstructorId(), e);
            return null;
        }
    }
}