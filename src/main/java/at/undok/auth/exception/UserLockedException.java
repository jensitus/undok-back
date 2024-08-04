package at.undok.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserLockedException extends RuntimeException {
    private final String message;
}
