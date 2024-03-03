package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.InteractionDto;
import courses.concordia.model.Interaction;

public class InteractionMapper {
    public static InteractionDto toDto(Interaction interaction) {
        return new InteractionDto()
                .setKind(InteractionDto.InteractionKind.valueOf(interaction.getKind().toValue()))
                .setCourseId(interaction.getCourseId())
                .setUserId(interaction.getUserId())
                .setReferrer(interaction.getReferrer());
    }
}
