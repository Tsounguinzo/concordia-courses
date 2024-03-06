package courses.concordia.service;

import courses.concordia.email.AbstractEmailContext;
import jakarta.mail.MessagingException;

import java.io.FileNotFoundException;

public interface EmailService {

    void sendEmail(final AbstractEmailContext email) throws MessagingException;
}
