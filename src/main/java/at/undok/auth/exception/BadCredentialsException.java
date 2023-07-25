package at.undok.auth.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String msg) {
        super(msg);
    }
}
