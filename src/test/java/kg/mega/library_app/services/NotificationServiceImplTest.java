package kg.mega.library_app.services;

import kg.mega.library_app.services.EmailService;
import kg.mega.library_app.services.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NotificationServiceImplTest {
    private NotificationServiceImpl notificationService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationServiceImpl(emailService);
    }

    @Test
    void testSendOverdueNotification() {
        String userEmail = "test@example.com";
        String bookTitle = "Book Title";

        notificationService.sendOverdueNotification(userEmail, bookTitle);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendMessage(emailCaptor.capture(), subjectCaptor.capture(), messageCaptor.capture());

        assertEquals(userEmail, emailCaptor.getValue());
        assertEquals("Notification to remind you to turn in your book", subjectCaptor.getValue());
        assertEquals("Dear Customer,Please return the book Book Title as the due date is approaching. Sincerely, Online Library.", messageCaptor.getValue());
    }

    @Test
    void testSendEmailNotification() {
        String email = "test@example.com";
        String subject = "Test Subject";
        String message = "Test Message";

        notificationService.sendEmailNotification(email, subject, message);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendMessage(emailCaptor.capture(), subjectCaptor.capture(), messageCaptor.capture());

        assertEquals(email, emailCaptor.getValue());
        assertEquals(subject, subjectCaptor.getValue());
        assertEquals(message, messageCaptor.getValue());
    }
}