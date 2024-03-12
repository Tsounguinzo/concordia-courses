package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.DeleteNotificationDto;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.UpdateNotificationDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.NotificationService;
import courses.concordia.service.implementation.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtServiceImpl jwtService;

    @Value("${app.jwt-name:accessToken}")
    private String tokenName;
    @GetMapping
    public Response<?> getNotifications(HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

         List<NotificationDto> notifications = notificationService.getNotifications(jwtService.extractUsername(token));
         return Response
                .ok()
                .setPayload(notifications);
    }

    @PutMapping
    public Response<?> updateNotification(@RequestBody UpdateNotificationDto updateNotificationDto, HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        notificationService.updateNotification(jwtService.extractUsername(token), updateNotificationDto.getCourseId(), updateNotificationDto.getCreatorId(), updateNotificationDto.isSeen());
        return Response.ok().setPayload("Notification was updated successfully");
    }

    @DeleteMapping
    public Response<?> deleteNotification(@RequestBody DeleteNotificationDto deleteNotificationDto, HttpServletRequest request) {

        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        notificationService.deleteNotification(jwtService.extractUsername(token), deleteNotificationDto.getCourseId());
        return Response.ok().setPayload("Notification was deleted successfully");
    }
}
