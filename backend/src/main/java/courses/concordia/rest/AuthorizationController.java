package courses.concordia.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

public class AuthorizationController {

    @GetMapping("/auth/user")
    public String checkAuthorization(@AuthenticationPrincipal OAuth2User principal) {
        if(principal != null){
            return "user is authorized";
        } else {
            return "user is not authorized";
        }
    }

    @GetMapping("/auth/login")
    public String login(){
        return "redirecting to Microsoft";
    }

    @GetMapping("/auth/logout")
    public String logout(){
        return "successfully logout";
    }

}

