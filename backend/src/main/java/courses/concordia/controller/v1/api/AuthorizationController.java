package courses.concordia.controller.v1.api;

import courses.concordia.controller.v1.request.AuthenticationRequest;
import courses.concordia.controller.v1.request.LoginRequest;
import courses.concordia.controller.v1.request.PasswordChangeRequest;
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.model.user.UserResponseDto;
import courses.concordia.dto.response.AuthenticationResponse;
import courses.concordia.dto.response.Response;
import courses.concordia.model.Token;
import courses.concordia.model.User;
import courses.concordia.repository.TokenRepository;
import courses.concordia.service.CookieService;
import courses.concordia.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final UserService userService;
    private final CookieService cookieService;
    private final TokenRepository tokenRepository;


    @GetMapping("/user")
    @CrossOrigin(origins = "*")
    public Response<?> getUser() {
        return Optional.ofNullable(userService.getAuthenticatedUser())
                .map(UserResponseDto::fromEntity)
                .map(Response.ok()::setPayload)
                .orElseGet(Response::unauthorized);
    }

    @PostMapping("/signin")
    public Response<?> signIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        userService.signIn(loginRequest, response);
        return Response.ok().setPayload("Boom! You're in");
    }

    @GetMapping("/signout")
    public Response<?> signOut(HttpServletRequest request, HttpServletResponse response) {
        userService.signOut(request, response);
        return Response.ok().setPayload("And you're out! Come back soon!");
    }

    @PostMapping("/signup")
    public Response<?> signUp(@Valid @RequestBody SignupRequest signupRequest, HttpServletResponse response) {

        UserDto userDto = new UserDto(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                signupRequest.getPassword(),
                false
        );
        AuthenticationResponse res = userService.signup(userDto);

        cookieService.addTokenCookies(response, res);

        return Response.ok().setPayload("Almost there! Just need to verify your email to make sure it's really you.");
    }

    @PostMapping("/authorized")
    public Response<?> confirmUserAccount(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        String token = authenticationRequest.getToken();
        Token t = tokenRepository.findByToken(token).orElse(null);

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

    @GetMapping("/resend_token")
    public Response<?> resendVerificationToken() {
        Optional<User> optionalUser = Optional.ofNullable(userService.getAuthenticatedUser());

        return optionalUser.map(User::getUsername)
                .map(username -> {
                    userService.resendToken(username);
                    return Response.ok().setPayload("Verification token resent to your email.");
                })
                .orElseGet(Response::unauthorized);
    }

    @GetMapping("/forgot_password")
    public Response<?> forgotPassword(@RequestParam String username) {
        userService.forgotPassword(username);
        return Response.ok().setPayload("Your reset password link is in your inbox!");
    }

    @GetMapping("/reset_password")
    public Response<?> resetPassword(@RequestParam String token) {
        Token t = tokenRepository.findByToken(token).orElse(null);

        if(t == null) {
            return Response.validationException();
        }

        if(t.isExpired()) {
            tokenRepository.delete(t);
            return Response.badRequest().setErrors("Whoops! Looks like this link has expired.");
        }

        String username = userService.verifyPasswordResetToken(token);
        return Response.ok().setPayload(username);
    }

    @PutMapping("/update_password")
    public Response<?> setNewPassword(@RequestParam String token, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        Token t = tokenRepository.findByToken(token).orElse(null);

        if(t == null) {
            return Response.validationException();
        }

        if(t.isExpired()) {
            tokenRepository.delete(t);
            return Response.badRequest().setErrors("Whoops! Looks like this link has expired.");
        }

        userService.changePassword(t, passwordChangeRequest.getNewPassword());
        return Response.ok().setPayload("Password updated successfully!");
    }
}