package kg.mega.library_app.events.listener;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kg.mega.library_app.events.RegistrationCompleteEvent;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserService userService;

    private final JavaMailSender mailSender;
    private User user;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String verificationToken = UUID.randomUUID().toString();
        userService.saveUserVerificationToken(user, verificationToken);
        String url = event.getApplicationUrl() + "/users/verify_email?token=" + verificationToken;
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException(e.getMessage());
        }
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        try {
            String subject = "Email Verification";
            String senderName = "User Registration Service";
            String mailContent = "<p> Hi, " + user.getFirstname() + ", </p>" +
                    "<p>Thank you for registering with us," +
                    "Please, follow the link below to complete your registration.</p>" +
                    "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                    "<p> Thank you <br> Library application Service";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom("springbootmailssendertesting@gmail.com", senderName);
            messageHelper.setTo(user.getEmail());
            messageHelper.setSubject(subject);
            messageHelper.setText(mailContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendException(e.getMessage());
        }
    }
}