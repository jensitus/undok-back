package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;

public class TooMuchCasesException extends UndokException {
    public TooMuchCasesException(String message) {
        super(message);
    }
}
