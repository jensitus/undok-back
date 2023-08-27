package at.undok.common.exception;

public class UndokException extends RuntimeException {
    private String message;

    public UndokException(String message) {
        super(message);
    }
}
