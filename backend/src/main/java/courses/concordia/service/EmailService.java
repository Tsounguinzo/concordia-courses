package courses.concordia.service;


public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);
    void sendNewTokenMailMessage(String name, String to, String token);
    void sendResetPasswordMailMessage(String name, String to, String token);
    void sendResetPasswordConfirmationMailMessage(String username, String email);
}
