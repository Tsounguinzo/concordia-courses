package courses.concordia.controller.api.v1;

import courses.concordia.dto.model.interaction.InteractionDto;
import courses.concordia.dto.model.interaction.UserInteractionsForCourseDto;
import courses.concordia.dto.model.interaction.UserInteractionsForInstructorDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.InteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/interactions")
public class InteractionController {
    private final InteractionService interactionService;

    @GetMapping("/referrer/{referrer}")
    public Response<?> getUserInteraction(@PathVariable String referrer) {
        List<InteractionDto> interactions = interactionService.getUserInteractions(referrer);
        return Response.ok().setPayload(interactions);
    }

    @GetMapping("/{id}/referrer/{referrer}")
    public Response<?> getUserInteractionsForCourseOrInstructor(@PathVariable String id, @PathVariable String referrer, @RequestParam String type) {
        log.info("fetching review interactions from {} for {}", referrer, id);
        List<InteractionDto> interactions = interactionService.getUserInteractions(id, referrer, type);
        Object payload = type.equals("course") ?
                new UserInteractionsForCourseDto().setCourseId(id).setReferrer(referrer).setInteractions(interactions)
                : new UserInteractionsForInstructorDto().setInstructorId(id).setReferrer(referrer).setInteractions(interactions);
        return Response
                .ok()
                .setPayload(payload);
    }

    @PostMapping
    public Response<?> addOrUpdateInteraction(@RequestBody InteractionDto interaction) {
        InteractionDto interactionDto = interactionService.addOrUpdateInteraction(interaction);
        return Response.ok().setPayload(interactionDto);
    }

    @DeleteMapping
    public Response<?> deleteInteraction(@RequestBody InteractionDto interaction) {
        interactionService.deleteInteraction(interaction);
        return Response.ok().setPayload("Interaction was deleted successfully");
    }
}
