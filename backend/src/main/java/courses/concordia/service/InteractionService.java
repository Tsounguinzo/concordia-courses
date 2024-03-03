package courses.concordia.service;

import courses.concordia.dto.model.course.InteractionDto;

import java.util.List;

public interface InteractionService {
    InteractionDto.InteractionKind getInteractionKind(InteractionDto interactionDto);

    List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer);

    InteractionDto addInteraction(InteractionDto interactionDto);

    void deleteInteraction(InteractionDto interactionDto);
}
