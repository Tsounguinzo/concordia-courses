package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.InteractionDto;
import courses.concordia.dto.model.course.UserInteractionsForCourseDto;
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
    @GetMapping
    public Response<?> getInteractionKind(@RequestParam String courseId, @RequestParam String userId, @RequestParam String referrer) {
        String kind = interactionService.getInteractionKind(courseId, userId, referrer);
        return Response.ok().setPayload(kind);
    }

    @GetMapping("/{courseId}/referrer/{referrer}")
    public Response<?> getUserInteractionsForCourse(@PathVariable String courseId, @PathVariable String referrer) {
        log.info("fetching review interactions from {} for course {}", referrer, courseId);
        List<InteractionDto> interactions = interactionService.getUserInteractionsForCourse(courseId, referrer);
        return Response
                .ok()
                .setPayload(new UserInteractionsForCourseDto()
                        .setCourseId(courseId)
                        .setReferrer(referrer)
                        .setInteractions(interactions));
    }

    @PostMapping
    public Response<?> addInteraction(@RequestBody InteractionDto interaction) {
        InteractionDto interactionDto = interactionService.addInteraction(interaction);
        return Response.ok().setPayload(interactionDto);
    }

    @DeleteMapping
    public Response<?> deleteInteraction(@RequestBody InteractionDto interaction) {
        interactionService.deleteInteraction(interaction.getCourseId(), interaction.getUserId(), interaction.getReferrer());
        return Response.ok().setPayload("Interaction was deleted successfully");
    }
}
