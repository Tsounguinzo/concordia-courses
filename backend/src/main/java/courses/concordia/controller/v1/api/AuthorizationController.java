package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.implementation.EmailServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final EmailServiceImpl emailService;
    private final UserServiceImpl userService;

    @PostMapping("/authorized")
    public String processSignup(@ModelAttribute("user") @Valid UserDto userDto,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "signUpForm";
        }
        try {
            emailService.sendSimpleEmail(userDto.getEmail(), "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            log.error("Error while sending out email..{}", mailException.getStackTrace());
            return "Unable to send email"+ HttpStatus.INTERNAL_SERVER_ERROR;
        }
        System.out.println(userDto);
        return "index";
    }



    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignupRequest signupRequest) {

        if (!signupRequest.getEmail().endsWith("concordia.ca")) {
            return ResponseEntity.badRequest().body("Email must be a Concordia email address.");
        }

        UserDto userDto = userService.signup(signupRequest);

        String token = userService.generateTokenForUser(userDto);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", "Bearer " + token);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(userDto);
    }

}

