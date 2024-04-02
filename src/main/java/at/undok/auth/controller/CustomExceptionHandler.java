package at.undok.auth.controller;

import at.undok.common.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    private final static String ACCESS_DENIED = " :: Perhaps you're locked. Please contact your administrator";
    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Message> handleAccessDenied(AccessDeniedException accessDeniedException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(accessDeniedException.getMessage() + ACCESS_DENIED));
    }
}
