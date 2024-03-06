package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.dto.model.course.SubscriptionPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping
    public Response<?> getSubscriptions(@RequestParam(name = "courseId", defaultValue = "") String courseId) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        if(courseId.isEmpty()) {
            List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptions(userId);
            return Response.ok().setPayload(subscriptions);
        } else {
            SubscriptionDto subscription = subscriptionService.getSubscription(userId, courseId);
            return Response.ok().setPayload(subscription);
        }
    }

    @PostMapping
    public Response<?> addSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        SubscriptionDto subscription = subscriptionService.addSubscription(userId, subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload(subscription);
    }

    @DeleteMapping
    public Response<?> deleteSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        subscriptionService.deleteSubscription(userId, subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload("SubscriptionMapper was deleted successfully");
    }
}