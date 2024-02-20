package courses.concordia.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthorizationController {

    @GetMapping("/authorized")
    public String checkAuthorization(@AuthenticationPrincipal OAuth2User principal) {
        if(principal != null){
            return "user is authorized";
        } else {
            return "user is not authorized";
        }
    }

    @GetMapping("/login")
    public String login(){
        return "redirecting to Microsoft";
    }

    @GetMapping("/logout")
    public String logout(){
        return "successfully logout";
    }

}

