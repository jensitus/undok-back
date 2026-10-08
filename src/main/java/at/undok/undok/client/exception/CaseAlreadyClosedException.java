package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;

public class CaseAlreadyClosedException extends UndokException {
    public CaseAlreadyClosedException(String message) {
        super(message);
    }
}
