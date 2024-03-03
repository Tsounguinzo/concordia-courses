package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping
    public Response<?> getNotifications(@RequestParam String userId) {
         List<NotificationDto> notifications = notificationService.getNotifications(userId);
         return Response
                .ok()
                .setPayload(notifications);
    }

    @PutMapping
    public Response<?> updateNotification(@RequestBody NotificationDto notification) {
        notificationService.updateNotification(notification);
        return Response.ok().setPayload("Notification was updated successfully");
    }

    @DeleteMapping
    public Response<?> deleteNotification(@RequestBody NotificationDto notification) {
        notificationService.deleteNotification(notification);
        return Response.ok().setPayload("Notification was deleted successfully");
    }
}
