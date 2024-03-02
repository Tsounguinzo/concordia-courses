package courses.concordia.controller.v1.api;

import courses.concordia.dto.model.user.UserDto;
import courses.concordia.service.implementation.EmailServiceImpl;
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



    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "signUpForm";
    }



}

