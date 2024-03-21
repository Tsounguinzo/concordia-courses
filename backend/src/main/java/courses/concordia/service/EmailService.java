package courses.concordia.service;


public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);
    void sendNewTokenMailMessage(String name, String to, String token);
}
