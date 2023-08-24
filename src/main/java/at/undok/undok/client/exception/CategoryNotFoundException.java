package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;

public class CategoryNotFoundException extends UndokException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
