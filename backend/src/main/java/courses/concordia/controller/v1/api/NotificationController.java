package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.course.DeleteNotificationDto;
import courses.concordia.dto.model.course.NotificationDto;
import courses.concordia.dto.model.course.UpdateNotificationDto;
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
    public Response<?> getNotifications() {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

         List<NotificationDto> notifications = notificationService.getNotifications(userId);
         return Response
                .ok()
                .setPayload(notifications);
    }

    @PutMapping
    public Response<?> updateNotification(@RequestBody UpdateNotificationDto updateNotificationDto) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        notificationService.updateNotification(userId, updateNotificationDto.getCourseId(), updateNotificationDto.getCreatorId(), updateNotificationDto.isSeen());
        return Response.ok().setPayload("Notification was updated successfully");
    }

    @DeleteMapping
    public Response<?> deleteNotification(@RequestBody DeleteNotificationDto deleteNotificationDto) {
        //TODO: get user id from jwt, for now using "Beaudelaire" as default userId
        String userId = "Beaudelaire";

        notificationService.deleteNotification(userId, deleteNotificationDto.getCourseId());
        return Response.ok().setPayload("Notification was deleted successfully");
    }
}
