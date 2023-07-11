package kg.mega.library_app.services;

public interface EmailService {
    void sendMessage(String to, String subject, String text);
}
