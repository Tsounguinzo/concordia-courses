package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.model.user.UserResponseDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Token;
import courses.concordia.repository.TokenRepository;
import courses.concordia.service.implementation.JwtServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import static courses.concordia.util.Misc.getTokenFromCookie;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final TokenRepository tokenRepository;

    @Value("${app.jwt-name:accessToken}")
    private String tokenName;

    @GetMapping("/user")
    public Response<?> getUser(HttpServletRequest request) {
        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }

        String username = jwtService.extractUsername(token);
        boolean isVerified = userService.isUserVerified(username);

        return Response.ok().setPayload(new UserResponseDto(username, isVerified));
    }

    @PostMapping("/signin")
    public Response<?> signIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthenticationResponse res = userService.authenticate(loginRequest);
        // set accessToken to cookie header
        int cookieExpiry = 1800;
        ResponseCookie cookie = ResponseCookie.from(tokenName, res.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(cookieExpiry)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Response.ok().setPayload("Boom! You're in");
    }

    @GetMapping("/signout")
    public Response<?> signOut(HttpServletRequest request, HttpServletResponse response) {
        String token = getTokenFromCookie(request, tokenName);
        if (token == null) {
            return Response.unauthorized();
        }
        String username = jwtService.extractUsername(token);
        //TODO: boolean isLogout = userService.logoutUser(username);

        //**** to be removed ******
        int cookieExpiry = 1;
        ResponseCookie cookie = ResponseCookie.from(tokenName, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJEYW5pZWwiLCJpYXQiOjE3MTA2MDM1ODgsImV4cCI6MTcxMDY4OTk4OH0.29prLTRokE0SOkzQ3SyOgYJOQYuLEYYJCdURdCYrwaw")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(cookieExpiry)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        //*********

        return Response.ok().setPayload("And you're out! Come back soon!");
    }

    @PostMapping("/signup")
    public Response<?> signUp(@Valid @RequestBody SignupRequest signupRequest) {

        if (!userService.checkIfUserExist(signupRequest.getUsername())) {
            /*if(!signupRequest.getEmail().endsWith("concordia.ca")){
                return Response.badRequest().setErrors("Email must be a Concordia email address");
            }*/
        }
        UserDto userDto = new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                false
        );
        userService.signup(userDto);
        return Response.ok().setPayload("Almost there! Just need to verify your email to make sure it's really you.");
    }

    @GetMapping("/authorized")
    public Response<?> confirmUserAccount(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        String token = authenticationRequest.getToken();
        Token t = tokenRepository.findByToken(token);

        if(t == null) {
            return Response.validationException().setPayload("Oops! Wrong token. Let's retry with the correct one.");
        }

        if(t.isExpired()) {
            //if the token is expired, delete the token
            tokenRepository.delete(t);
            return Response.badRequest().setPayload("Whoops! Looks like this token has expired. Time to grab a fresh one!");
        }

        userService.verifyToken(token);
        return Response.ok().setPayload("Welcome aboard! You've successfully joined the cool zone.");
    }
}

