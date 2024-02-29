package courses.concordia.controller.v1.api;

import courses.concordia.model.User;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public User getUser() {
        return userService.getCurrentUser();
    }
}
