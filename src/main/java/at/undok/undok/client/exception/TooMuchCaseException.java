package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;

public class TooMuchCaseException extends UndokException {
    public TooMuchCaseException(String message) {
        super(message);
    }
}
