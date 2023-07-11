package kg.mega.library_app.common;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.common.exceptions.MailSendingException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j

@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> InvalidArgumentException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return errorMap;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unexpectedException(Exception ex) {
        HttpStatus serverError = INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException(ex.getMessage(), serverError);
        return new ResponseEntity<>(apiException, serverError);
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<?> psqlException(PSQLException ex) {
        HttpStatus forbidden = FORBIDDEN;
        ApiException apiException = new ApiException(ex.getMessage(), forbidden);
        return new ResponseEntity<>(apiException, forbidden);
    }

    @ExceptionHandler(value = {DataNotFoundException.class})
    public ResponseEntity<?> dataNotFoundException(DataNotFoundException ex) {
        HttpStatus notFound = NOT_FOUND;
        ApiException apiException = new ApiException(ex.getMessage(), notFound);
        return new ResponseEntity<>(apiException, notFound);
    }

    @ExceptionHandler(value = {DuplicateException.class})
    public ResponseEntity<?> duplicateException(DuplicateException ex) {
        HttpStatus conflict = CONFLICT;
        ApiException apiException = new ApiException(ex.getMessage(), conflict);
        return new ResponseEntity<>(apiException, conflict);
    }

    @ExceptionHandler(value = {MailSendException.class})
    public ResponseEntity<?> mailSendingException(MailSendingException ex) {
        HttpStatus serverError = INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException(ex.getMessage(), serverError);
        return new ResponseEntity<>(apiException, serverError);
    }

    @ExceptionHandler(value = {InadmissibleEditingException.class})
    public ResponseEntity<?> inadmissibleEditingException(InadmissibleEditingException ex) {
        HttpStatus forbidden = FORBIDDEN;
        ApiException apiException = new ApiException(ex.getMessage(), forbidden);
        return new ResponseEntity<>(apiException, forbidden);
    }
}
