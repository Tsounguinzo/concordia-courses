package courses.concordia.controller.v1.api;

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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/v1/auth")
public class AuthorizationController {

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    UserServiceImpl userService;


    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationController.class);
    @PostMapping("/authorized")
    public String processSignup(@ModelAttribute("user") @Valid UserDto userDto,
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
    public Response showSignupForm(@RequestBody SignupRequest signupRequest) {
        UserDto userDto;

        if(signupRequest.getEmail().contains("concordia.ca")){
            try {
                emailService.sendSimpleEmail(signupRequest.getEmail(),
                        "Message",
                        "This is a welcome email for you!");
            }catch (MailException mailException) {
                LOG.error("Error while sending out email..{}", mailException.getStackTrace());
                return Response.ok();
            }
        }

        try{
            userDto = userService.signup(new UserDto(
                    signupRequest.getUsername(),
                    signupRequest.getEmail(),
                    signupRequest.getPassword()
            ));
        }catch (Exception e){
            return Response.duplicateEntity();
        }

        return Response.ok().setPayload(userDto);
    }



}

