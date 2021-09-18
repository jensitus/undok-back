package at.undok.undok.client.exception;

import org.springframework.http.HttpStatus;

public class KeywordNotFoundException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorMessage;

    public KeywordNotFoundException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.errorMessage = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
