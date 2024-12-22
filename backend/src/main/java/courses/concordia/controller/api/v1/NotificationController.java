package courses.concordia.controller.api.v1;

import courses.concordia.dto.model.notification.DeleteNotificationDto;
import courses.concordia.dto.model.notification.NotificationDto;
import courses.concordia.dto.model.notification.UpdateNotificationDto;
import courses.concordia.dto.response.Response;
import courses.concordia.model.User;
import courses.concordia.service.NotificationService;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    @GetMapping
    public Response<?> getNotifications() {
        User user = userService.getAuthenticatedUser();

        if(user == null) {
            return Response.unauthorized();
        }

         List<NotificationDto> notifications = notificationService.getNotifications(user.get_id());
         return Response
                .ok()
                .setPayload(notifications);
    }

    @PutMapping
    public Response<?> updateNotification(@RequestBody UpdateNotificationDto updateNotificationDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }
        notificationService.updateNotification(user.get_id(), updateNotificationDto.getCourseId(), updateNotificationDto.getCreatorId(), updateNotificationDto.isSeen());
        return Response.ok().setPayload("Notification was updated successfully");
    }

    @DeleteMapping
    public Response<?> deleteNotification(@RequestBody DeleteNotificationDto deleteNotificationDto) {

        User user = userService.getAuthenticatedUser();
        if(user == null) {
            return Response.unauthorized();
        }

        notificationService.deleteNotification(deleteNotificationDto.getCreatorId(), user.get_id(), deleteNotificationDto.getCourseId());
        return Response.ok().setPayload("Notification was deleted successfully");
    }
}
