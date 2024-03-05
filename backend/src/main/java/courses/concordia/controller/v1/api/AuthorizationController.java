package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.service.implementation.AuthenticationServiceImpl;
import courses.concordia.service.implementation.EmailServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    UserServiceImpl userService;

    private final AuthenticationServiceImpl authenticationService;


    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);
    @PostMapping("/authorized")
    public String authorized(@ModelAttribute("user") @Valid UserDto userDto,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "signUpForm";
        }
        try {
            emailService.sendSimpleEmail(userDto.getEmail(), "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return "Unable to send email"+ HttpStatus.INTERNAL_SERVER_ERROR;
        }
        System.out.println(userDto);
        return "index";
    }


    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        UserDto userDto;

        /*if(!signupRequest.getEmail().endsWith("concordia.ca")){
            return Response.badRequest().setErrors("Email must be a Concordia email address");
        }*/

        userService.signup(new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword()
        ));
        return ResponseEntity.ok(authenticationService.signup(signupRequest));
    }
}

