package courses.concordia.service;

import courses.concordia.dto.model.course.InteractionDto;
import courses.concordia.model.Interaction;

import java.util.List;

public interface InteractionService {
    String getInteractionKind(String courseId, String userId, String referrer);

    List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer);

    InteractionDto addOrUpdateInteraction(InteractionDto interactionDto);

    void deleteInteraction(String courseId, String userId, String referrer);

    void deleteInteractions(String courseId, String userId);

    List<Interaction> interactionsForReview(String courseId, String userId);
}
