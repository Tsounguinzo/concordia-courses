package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.IdDto;
import courses.concordia.dto.model.course.ReviewDto;
import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Review;
import courses.concordia.service.NotificationService;
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
    public Response<?> getSubscriptions(@RequestBody SubscriptionDto subscriptionDto) {
        List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptions(subscriptionDto);
        return Response.ok().setPayload(subscriptions);
    }

    @PostMapping
    public Response<?> addSubscription(@RequestBody SubscriptionDto subscriptionDto) {
        SubscriptionDto subscription = subscriptionService.addSubscription(subscriptionDto);
        return Response.ok().setPayload(subscription);
    }

    @DeleteMapping
    public Response<?> deleteSubscription(@RequestBody SubscriptionDto subscriptionDto) {
        subscriptionService.deleteSubscription(subscriptionDto);
        return Response.ok().setPayload("SubscriptionMapper was deleted successfully");
    }
}