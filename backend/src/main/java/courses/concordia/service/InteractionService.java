package courses.concordia.service;

import courses.concordia.dto.model.interaction.InteractionDto;

import java.util.List;

public interface InteractionService {
    List<InteractionDto> getUserInteractions(String id, String referrer, String type);

    InteractionDto addOrUpdateInteraction(InteractionDto interactionDto);

    void deleteInteraction(InteractionDto interactionDto);

    void deleteInteractions(String id, String userId, String type);

    List<InteractionDto> getUserInteractions(String referrer);

}
