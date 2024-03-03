package courses.concordia.service.implementation;

import courses.concordia.dto.mapper.InteractionMapper;
import courses.concordia.dto.model.course.InteractionDto;
import courses.concordia.exception.CCException;
import courses.concordia.exception.EntityType;
import courses.concordia.exception.ExceptionType;
import courses.concordia.model.Interaction;
import courses.concordia.repository.InteractionRepository;
import courses.concordia.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static courses.concordia.exception.EntityType.INTERACTION;
import static courses.concordia.exception.ExceptionType.ENTITY_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Service
public class InteractionServiceImpl implements InteractionService {
    private final InteractionRepository interactionRepository;
    @Override
    public InteractionDto.InteractionKind getInteractionKind(InteractionDto interactionDto) {
            Optional<Interaction> interaction = interactionRepository.findByCourseIdAndUserIdAndReferrer(
                    interactionDto.getCourseId(), interactionDto.getUserId(), interactionDto.getReferrer());

            if (interaction.isPresent()) {
                return InteractionMapper.toDto(interaction.get()).getKind();
            }

            throw exception(INTERACTION, ENTITY_NOT_FOUND);
    }

    @Override
    public List<InteractionDto> getUserInteractionsForCourse(String courseId, String referrer) {
        return interactionRepository.findByCourseIdAndReferrer(courseId, referrer).stream()
                .map(InteractionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public InteractionDto addInteraction(InteractionDto interactionDto) {
        Interaction interaction = new Interaction()
                .setKind(Interaction.InteractionKind.valueOf(interactionDto.getKind().getValue()))
                .setCourseId(interactionDto.getCourseId())
                .setUserId(interactionDto.getUserId())
                .setReferrer(interactionDto.getReferrer());
        interaction = interactionRepository.save(interaction);
        return InteractionMapper.toDto(interaction);
    }

    @Override
    public void deleteInteraction(InteractionDto interactionDto) {
        Optional<Interaction> interaction = interactionRepository.findByCourseIdAndUserIdAndReferrer(
                        interactionDto.getCourseId(), interactionDto.getUserId(), interactionDto.getReferrer());
        if (interaction.isPresent()) {
            interactionRepository.delete(interaction.get());
        } else {
            throw exception(INTERACTION, ENTITY_NOT_FOUND);
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return CCException.throwException(entityType, exceptionType, args);
    }
}
