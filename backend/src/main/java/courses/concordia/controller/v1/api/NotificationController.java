package courses.concordia.controller.v1.api;

import courses.concordia.config.JwtConfigProperties;
import courses.concordia.dto.model.course.DeleteNotificationDto;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.UpdateNotificationDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.NotificationService;
import courses.concordia.service.UserService;
import courses.concordia.service.implementation.JwtServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    private final JwtServiceImpl jwtService;
    private final JwtConfigProperties jwtConfigProperties;
    @GetMapping
    public Response<?> getNotifications(HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
         List<NotificationDto> notifications = notificationService.getNotifications(userId);
         return Response
                .ok()
                .setPayload(notifications);
    }

    @PutMapping
    public Response<?> updateNotification(@RequestBody UpdateNotificationDto updateNotificationDto, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        notificationService.updateNotification(userId, updateNotificationDto.getCourseId(), updateNotificationDto.getCreatorId(), updateNotificationDto.isSeen());
        return Response.ok().setPayload("Notification was updated successfully");
    }

    @DeleteMapping
    public Response<?> deleteNotification(@RequestBody DeleteNotificationDto deleteNotificationDto, HttpServletRequest request) {

        String userId = getUserIdFromToken(request);
        if (userId == null) {
            return Response.unauthorized();
        }
        notificationService.deleteNotification(deleteNotificationDto.getCreatorId(), userId, deleteNotificationDto.getCourseId());
        return Response.ok().setPayload("Notification was deleted successfully");
    }

    private String getUserIdFromToken(HttpServletRequest request) {
        String token = getTokenFromCookie(request, jwtConfigProperties.getTokenName());
        if (token == null) {
            return null;
        }
        String username = jwtService.extractUsername(token);
        return userService.getUserIdFromUsername(username);
    }
}
