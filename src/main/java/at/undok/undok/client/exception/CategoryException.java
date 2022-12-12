package at.undok.undok.client.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class CategoryException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

}
