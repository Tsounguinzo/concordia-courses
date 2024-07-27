package courses.concordia.service;


public interface EmailService {
    void sendSuccessMailMessage(String name, String to, String startTime, String endTime, int recordCount);
    void sendFailureMailMessage(String name, String to, String errorMessage);
    void sendSimpleMailMessage(String name, String to, String token);
    void sendNewTokenMailMessage(String name, String to, String token);
    void sendResetPasswordMailMessage(String name, String to, String token);
    void sendResetPasswordConfirmationMailMessage(String username, String email);
}
