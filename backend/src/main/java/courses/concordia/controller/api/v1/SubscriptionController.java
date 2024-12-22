package courses.concordia.controller.api.v1;

import courses.concordia.dto.model.subscription.SubscriptionDto;
import courses.concordia.dto.model.subscription.SubscriptionPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.User;
import courses.concordia.service.SubscriptionService;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;

    @GetMapping
    public Response<?> getSubscriptions(@RequestParam(name = "courseId", defaultValue = "") String courseId) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        if(courseId.isEmpty()) {
            List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptions(user.get_id());
            return Response.ok().setPayload(subscriptions);
        } else {
            SubscriptionDto subscription = subscriptionService.getSubscription(user.get_id(), courseId);
            return Response.ok().setPayload(subscription);
        }
    }

    @PostMapping
    public Response<?> addSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        SubscriptionDto subscription = subscriptionService.addSubscription(user.get_id(), subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload(subscription);
    }

    @DeleteMapping
    public Response<?> deleteSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        subscriptionService.deleteSubscription(user.get_id(), subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload("Subscription was deleted successfully");
    }
}