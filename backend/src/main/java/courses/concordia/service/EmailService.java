package courses.concordia.service;


public interface EmailService {
    void sendSimpleMailMessage(String name, String to, String token);
    //void sendHtmlEmail(String name, String to, String token);
    //void sendHtmlEmailWithEmbeddedFiles(String name, String to, String token);
}
