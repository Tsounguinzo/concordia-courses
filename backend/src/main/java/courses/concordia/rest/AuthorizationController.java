package courses.concordia.rest;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @GetMapping("/authorized")
    public Map<String, Object> authorize(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/login")
    public void Login(HttpServletResponse response) throws IOException {
        String authorizationUri = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
                + "?client_id=fdb53ef1-b2d0-4f6c-afad-406a0d15bd41"
                + "&response_type=code"
                + "&redirect_uri=http://localhost:9191/"
                + "&response_mode=query"
                + "&scope=https://graph.microsoft.com/.default"
                + "&state=12345";
        response.sendRedirect(authorizationUri);
    }

    @GetMapping("/logout")
    public String logout(){
        return "successfully logout";
    }

}

