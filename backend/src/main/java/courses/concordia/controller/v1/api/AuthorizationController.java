package courses.concordia.controller.v1.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthorizationController {

    @GetMapping("/authorized")
    public Map<String, Object> authorize(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/login")
    public String Login() {
        return "???";
    }

    @GetMapping("/logout")
    public String logout(){
        return "successfully logout";
    }

}

