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

    @PostMapping("/user")
    public Response<?> getUser(HttpServletRequest request) {
        String token = getTokenFromCookie(request, tokenName);
        System.out.println(token);
        if (token == null) {
            return Response.unauthorized();
        }
        return Response.ok().setPayload(jwtService.extractUsername(token));
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
        return Response.ok().setPayload("Login Successful");
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
        return Response.ok().setPayload("Account Successfully verified, You can now login");
    }
}

