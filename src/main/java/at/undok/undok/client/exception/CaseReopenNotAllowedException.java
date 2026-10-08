package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;

/**
 * Reopening is refused: the case is not closed, or the client already has an open case.
 * A client must never end up with two open cases — see CaseService#updateCase.
 */
public class CaseReopenNotAllowedException extends UndokException {
    public CaseReopenNotAllowedException(String message) {
        super(message);
    }
}
