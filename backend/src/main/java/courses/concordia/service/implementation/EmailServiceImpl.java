package courses.concordia.service.implementation;

import courses.concordia.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String NEW_VERIFICATION_TOKEN = "New Verification Code";
    public static final String RESET_PASSWORD_TOKEN = "Reset Password";
    public static final String RESET_PASSWORD_CONFIRMATION = "Password Reset Confirmation";
    public static final String UTF_8_ENCODING = "UTF-8";

    /**
     * Sends a simple HTML email for account verification to a new user.
     *
     * @param name  The name of the recipient.
     * @param to    The recipient's email address.
     * @param token The verification token to be included in the email.
     */
    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        sendHtmlEmail(name, to, token, NEW_USER_ACCOUNT_VERIFICATION, "newUserVerification");
    }

    /**
     * Sends an HTML email containing a new verification token.
     *
     * @param name  The name of the recipient.
     * @param to    The recipient's email address.
     * @param token The new verification token.
     */
    @Override
    public void sendNewTokenMailMessage(String name, String to, String token) {
        sendHtmlEmail(name, to, token, NEW_VERIFICATION_TOKEN, "newTokenVerification");
    }

    /**
     * Sends an HTML email for resetting the password.
     *
     * @param name  The name of the recipient.
     * @param to    The recipient's email address.
     * @param token The password reset token.
     */
    @Override
    public void sendResetPasswordMailMessage(String name, String to, String token) {
        sendHtmlEmail(name, to, token, RESET_PASSWORD_TOKEN, "resetPassword");
    }

    /**
     * Sends a confirmation email after a successful password reset.
     *
     * @param username The username of the recipient.
     * @param email    The recipient's email address.
     */
    @Override
    public void sendResetPasswordConfirmationMailMessage(String username, String email) {
        sendHtmlEmail(username, email, null, RESET_PASSWORD_CONFIRMATION, "resetPasswordConfirmation");
    }

    /**
     * Helper method to send an HTML email using Thymeleaf template engine.
     *
     * @param name         The name of the recipient.
     * @param to           The recipient's email address.
     * @param token        The token to be included in the email, if applicable.
     * @param subject      The subject of the email.
     * @param templateName The name of the Thymeleaf template to be used.
     */
    private void sendHtmlEmail(String name, String to, String token, String subject, String templateName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("token", token);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setFrom("cdpl4ter@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
