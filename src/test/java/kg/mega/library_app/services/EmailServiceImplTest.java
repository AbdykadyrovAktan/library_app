package kg.mega.library_app.services;

import kg.mega.library_app.services.EmailService;
import kg.mega.library_app.services.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Test
    void testSendMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";

        emailService.sendMessage(to, subject, text);

        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(to, Objects.requireNonNull(sentMessage.getTo())[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(text, sentMessage.getText());
    }
}