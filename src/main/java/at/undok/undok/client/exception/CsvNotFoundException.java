package at.undok.undok.client.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class CsvNotFoundException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorMessage;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
