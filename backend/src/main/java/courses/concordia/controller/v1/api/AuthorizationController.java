package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.User;
import courses.concordia.service.implementation.EmailServiceImpl;
import courses.concordia.service.implementation.JwtServiceImpl;
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


    private final EmailServiceImpl emailService;
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;

    public String token;


    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);
    @PostMapping("/user")
    public Response<?> getuser() {
        return Response.ok().setPayload(jwtService.extractUsername(token));
    }

    @PostMapping("/signin")
    public Response<?> signin(@Valid @RequestBody LoginRequest loginRequest){
        AuthenticationResponse res = userService.authenticate(loginRequest);
        token = res.getToken();
        return Response.ok().setPayload(res);
    }


    @PostMapping("/signup")
    public Response<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthenticationResponse res;
        UserDto userDto;

        if(!userService.checkIfUserExist(signupRequest.getUsername())){
            if(!signupRequest.getEmail().endsWith("concordia.ca")){
                return Response.badRequest().setErrors("Email must be a Concordia email address");
            }

        }
        userDto = new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                false
        );
        res = userService.signup(userDto);
        token = res.getToken();
        return Response.ok().setPayload(res);

    }
}

