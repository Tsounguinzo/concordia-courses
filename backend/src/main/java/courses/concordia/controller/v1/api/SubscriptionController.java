package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.dto.model.course.SubscriptionPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.SubscriptionService;
import courses.concordia.service.implementation.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final JwtServiceImpl jwtService;

    @Value("${app.jwt-name:accessToken}")
    private String tokenName;

    @GetMapping
    public Response<?> getSubscriptions(@RequestParam(name = "courseId", defaultValue = "") String courseId, HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        if(courseId.isEmpty()) {
            List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptions(jwtService.extractUsername(token));
            return Response.ok().setPayload(subscriptions);
        } else {
            SubscriptionDto subscription = subscriptionService.getSubscription(jwtService.extractUsername(token), courseId);
            return Response.ok().setPayload(subscription);
        }
    }

    @PostMapping
    public Response<?> addSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto, HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        SubscriptionDto subscription = subscriptionService.addSubscription(jwtService.extractUsername(token), subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload(subscription);
    }

    @DeleteMapping
    public Response<?> deleteSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto, HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        subscriptionService.deleteSubscription(jwtService.extractUsername(token), subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload("SubscriptionMapper was deleted successfully");
    }
}