package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
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


    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);
    @PostMapping("/authorized")
    public String authorized(@Valid @RequestBody
                                 AuthenticationResponse authenticationResponse) {
        return "";
    }

    @PostMapping("/signin")
    public Response<?> signin(@Valid @RequestBody LoginRequest loginRequest){
        return Response.ok().setPayload(userService.authenticate(loginRequest));
    }


    @PostMapping("/signup")
    public Response<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        UserDto userDto;

        if(!signupRequest.getEmail().endsWith("concordia.ca")){
            return Response.badRequest().setErrors("Email must be a Concordia email address");
        }

        userDto = new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );
        return Response.ok().setPayload(userService.signup(userDto));
    }
}

