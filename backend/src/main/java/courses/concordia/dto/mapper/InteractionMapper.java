package courses.concordia.dto.mapper;

import courses.concordia.dto.model.interaction.InteractionDto;
import courses.concordia.model.Interaction;

public class InteractionMapper {
    public static InteractionDto toDto(Interaction interaction) {
        return new InteractionDto()
                .setKind(interaction.getKind().toValue())
                .setType(interaction.getType())
                .setCourseId(interaction.getCourseId())
                .setInstructorId(interaction.getInstructorId())
                .setUserId(interaction.getUserId())
                .setReferrer(interaction.getReferrer());
    }
}
