package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Token;
import courses.concordia.repository.TokenRepository;
import courses.concordia.service.implementation.EmailServiceImpl;
import courses.concordia.service.implementation.JwtServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final TokenRepository tokenRepository;
    public String token;


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
        res = userService.signup(userDto);
        token = res.getToken();
        return Response.ok().setPayload(res);

    }

    @GetMapping("/authorized")
    public Response<?> confirmUserAccount(@Valid @RequestParam("token") String token){
        userService.verifyToken(token);
        Token t = tokenRepository.findByToken(token);

        System.out.println(t.getUser());
        System.out.println(token);
        return Response.ok().setPayload(
                AuthenticationResponse.builder()
                .token(null)
                .build());
    }
}

