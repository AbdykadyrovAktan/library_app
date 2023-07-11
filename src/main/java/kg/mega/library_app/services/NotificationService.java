package kg.mega.library_app.services;

public interface NotificationService {
    void sendOverdueNotification(String userEmail, String bookTitle);

    void sendEmailNotification(String email, String subject, String message);
}
