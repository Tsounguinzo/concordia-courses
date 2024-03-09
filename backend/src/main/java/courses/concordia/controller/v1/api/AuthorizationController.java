package courses.concordia.controller.v1.api;

<<<<<<< Updated upstream
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
=======
import courses.concordia.controller.v1.request.SignupRequest;
import courses.concordia.dto.model.user.UserDto;
import courses.concordia.dto.response.Response;
import courses.concordia.service.implementation.EmailServiceImpl;
import courses.concordia.service.implementation.UserServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
>>>>>>> Stashed changes


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthorizationController {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final TokenRepository tokenRepository;
    public String token;

<<<<<<< Updated upstream

    @PostMapping("/user")
    public Response<?> getuser() {
        return Response.ok().setPayload(jwtService.extractUsername(token));
    }

    @PostMapping("/signin")
    public Response<?> signin(@Valid @RequestBody LoginRequest loginRequest){
        AuthenticationResponse res = userService.authenticate(loginRequest);
        token = res.getToken();
        return Response.ok().setPayload(res);
=======
    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    UserServiceImpl userService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);
    @PostMapping("/authorized")
    public Response authorized(@RequestBody UserDto userDto) {
        try {
            emailService.sendSimpleEmail(userDto.getEmail(), "Welcome", "This is a welcome email for your!!");
        } catch (MailException mailException) {
            LOG.error("Error while sending out email..{}", mailException.getStackTrace());
            return Response.exception();
        }
        System.out.println(userDto);
        return Response.ok();
>>>>>>> Stashed changes
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
        userService.signup(userDto);
        return Response.ok().setPayload("successfully sign up, please verify your email address.");

<<<<<<< Updated upstream
=======

    @PostMapping("/signup")
    public Response signup(@RequestBody SignupRequest signupRequest) {
        UserDto userDto;

        if(signupRequest.getEmail().contains("concordia.ca")){
            try {
                emailService.sendSimpleEmail(signupRequest.getEmail(),
                        "Welcome",
                        "This is a welcome email for your!!");
            } catch (MailException mailException) {
                LOG.error("Error while sending out email..{}", mailException.getStackTrace());
                return Response.exception();
            }
        }else{
            return Response.ok();
        }

        try {
            userDto = userService.signup(
                    new UserDto(signupRequest.getUsername(),
                            signupRequest.getEmail(),
                            signupRequest.getPassword()));
        }catch (Exception e){
            return Response.duplicateEntity();
        }

        return Response.ok().setPayload(userDto);
>>>>>>> Stashed changes
    }

    @GetMapping("/authorized")
    public Response<?> confirmUserAccount(@Valid @RequestParam("token") String token){
        System.out.println(token);

        userService.verifyToken(token);

        Token t = tokenRepository.findByToken(token);
        String jwtToken = jwtService.generateToken(t.getUser());
        //System.out.println(t.getUser());
        return Response.ok().setPayload(
                AuthenticationResponse.builder()
                .token(jwtToken)
                .build());
    }
}

