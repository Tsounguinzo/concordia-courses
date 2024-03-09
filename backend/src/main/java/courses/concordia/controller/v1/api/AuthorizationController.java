package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Token;
import courses.concordia.repository.TokenRepository;
import courses.concordia.service.implementation.JwtServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final TokenRepository tokenRepository;
    public String token;

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);

    @PostMapping("/user")
    public Response<?> getuser() {
        return Response.ok().setPayload(jwtService.extractUsername(token));
    }

    @PostMapping("/signin")
    public Response<?> signin(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResponse res = userService.authenticate(loginRequest);
        token = res.getToken();
        return Response.ok().setPayload(res);
    }

    @PostMapping("/signup")
    public Response<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        AuthenticationResponse res;
        UserDto userDto;

        if (!userService.checkIfUserExist(signupRequest.getUsername())) {
            /*if(!signupRequest.getEmail().endsWith("concordia.ca")){
                return Response.badRequest().setErrors("Email must be a Concordia email address");
            }*/
        }
        userDto = new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                false
        );
        userService.signup(userDto);
        return Response.ok().setPayload("successfully sign up, please verify your email address.");
    }

    @GetMapping("/authorized")
    public Response<?> confirmUserAccount(@Valid @RequestParam("token") String token) {
        Token t = tokenRepository.findByToken(token);

        if(t.isExpired()) {
            //if the token is expired, delete the token
            tokenRepository.delete(t);
            return Response.badRequest().setPayload("The link is expired.");
        }

        userService.verifyToken(token);

        String jwtToken = jwtService.generateToken(t.getUser());
        //System.out.println(t.getUser());
        return Response.ok().setPayload(
                AuthenticationResponse.builder()
                        .token(jwtToken)
                        .build());
    }
}

