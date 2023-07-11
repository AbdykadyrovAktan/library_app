package kg.mega.library_app.services.impl;

import kg.mega.library_app.services.EmailService;
import kg.mega.library_app.services.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j

@Service
public class NotificationServiceImpl implements NotificationService {
    private final EmailService emailService;

    @Autowired
    public NotificationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendOverdueNotification(String userEmail, String bookTitle) {
        String subject = "Notification to remind you to turn in your book";
        String message = "Dear Customer,Please return the book {" + bookTitle +
                "} as the due date is approaching. Sincerely, Online Library.";
        emailService.sendMessage(userEmail, subject, message);
    }

    @Override
    public void sendEmailNotification(String email, String subject, String message) {
        emailService.sendMessage(email, subject, message);
    }
}
