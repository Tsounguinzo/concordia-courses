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
    public static final String UTF_8_ENCODING = "UTF-8";
    @Override
    @Async
    public void sendSimpleMailMessage(String name, String to, String token) {
        sendHtmlEmail(name, to, token, NEW_USER_ACCOUNT_VERIFICATION, "newUserVerification");
    }

    @Override
    public void sendNewTokenMailMessage(String name, String to, String token) {
        sendHtmlEmail(name, to, token, NEW_VERIFICATION_TOKEN, "newTokenVerification");
    }

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
