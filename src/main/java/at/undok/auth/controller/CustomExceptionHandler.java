package at.undok.auth.controller;

import at.undok.auth.exception.TokenExpiredException;
import at.undok.common.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final static String TOKEN_EXPIRED = " :: please login again";

    @ExceptionHandler(TokenExpiredException.class)
    public final ResponseEntity<Message> handleTokenExpiredException(TokenExpiredException tokenExpiredException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(tokenExpiredException.getMessage() + TOKEN_EXPIRED));
    }

}
