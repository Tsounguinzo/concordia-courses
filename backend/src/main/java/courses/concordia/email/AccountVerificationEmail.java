package courses.concordia.email;

import courses.concordia.model.User;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountVerificationEmail extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        User customer = (User) context; // we pass the customer informati
        put("userName", customer.getUsername());
        setTemplateLocation("emails/email-verification");
        setSubject("Complete your registration");
        setFrom("cdpl4ter@gmail.com");
        setTo(customer.getEmail());
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/register/verify").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}