package at.undok.undok.client.exception;

import org.springframework.http.HttpStatus;

public class KeywordException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public KeywordException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
