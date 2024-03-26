package courses.concordia.controller.v1.api;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.config.TokenType;
import courses.concordia.dto.model.course.SubscriptionDto;
import courses.concordia.dto.model.course.SubscriptionPayloadDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.SubscriptionService;
import courses.concordia.service.UserService;
import courses.concordia.service.implementation.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final JwtServiceImpl jwtService;
    private final JwtConfigProperties jwtConfigProperties;

    @GetMapping
    public Response<?> getSubscriptions(@RequestParam(name = "courseId", defaultValue = "") String courseId, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        if(courseId.isEmpty()) {
            List<SubscriptionDto> subscriptions = subscriptionService.getSubscriptions(userId);
            return Response.ok().setPayload(subscriptions);
        } else {
            SubscriptionDto subscription = subscriptionService.getSubscription(userId, courseId);
            return Response.ok().setPayload(subscription);
        }
    }

    @PostMapping
    public Response<?> addSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        SubscriptionDto subscription = subscriptionService.addSubscription(userId, subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload(subscription);
    }

    @DeleteMapping
    public Response<?> deleteSubscription(@RequestBody SubscriptionPayloadDto subscriptionPayloadDto, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        subscriptionService.deleteSubscription(userId, subscriptionPayloadDto.getCourseId());
        return Response.ok().setPayload("Subscription was deleted successfully");
    }

    private String getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromCookie(request, jwtConfigProperties.getTokenName());
        if (token == null) {
            return null;
        }
        String username = jwtService.extractUsername(token, TokenType.accessToken);
        return userService.getUserIdFromUsername(username);
    }
}