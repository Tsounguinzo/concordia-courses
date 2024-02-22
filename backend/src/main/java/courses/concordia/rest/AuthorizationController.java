package courses.concordia.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @GetMapping("/authorized")
    public Map<String, Object> authorize(@AuthenticationPrincipal OAuth2User principal) {
        //if the user is authorized then get name
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

}

