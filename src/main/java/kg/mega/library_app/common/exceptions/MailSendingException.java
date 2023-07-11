package kg.mega.library_app.common.exceptions;

import org.springframework.mail.MailSendException;

public class MailSendingException extends MailSendException {
    public MailSendingException(String msg) {
        super(msg);
    }
}
