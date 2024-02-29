package courses.concordia.controller.v1.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    @GetMapping("/authorized")
    public Map<String, String> authorize(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, String> map = new HashMap<>();

        String email = principal.getAttribute("email");
        if(email != null){
            if(email.contains("concordia")){
                map.put("name", principal.getAttribute("name"));
                map.put("email",principal.getAttribute("email"));
                //if the user is authorized then get name and email
                return map;
            }
        }
        //test
        map.put("name", principal.getAttribute("name"));
        map.put("email",principal.getAttribute("email"));
        return map;

        //return null;
    }
}

