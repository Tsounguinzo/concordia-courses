package courses.concordia.service;

import courses.concordia.dto.model.interaction.InteractionDto;

import java.util.List;

public interface InteractionService {
    String getInteractionKind(String courseId, String userId, String referrer);

    List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer);

    InteractionDto addOrUpdateInteraction(InteractionDto interactionDto, String ReviewType);

    void deleteInteraction(InteractionDto interactionDto, String ReviewType);

    void deleteInteractions(String id, String userId);

    List<InteractionDto> getUserInteractions(String referrer);

    List<InteractionDto> getUserInteractionsForInstructor(String instructorId, String referrer);
}
