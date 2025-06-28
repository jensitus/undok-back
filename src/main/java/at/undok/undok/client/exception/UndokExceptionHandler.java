package at.undok.undok.client.exception;

import at.undok.common.exception.UndokException;
import at.undok.common.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UndokExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Message> handleCategoryNotFound(CategoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(e.getMessage()));
    }

    @ExceptionHandler(CounselingDateException.class)
    public ResponseEntity<Message> handleCounselingDateException(CounselingDateException e) {
        return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED).body(new Message(e.getMessage()));
    }

    @ExceptionHandler(TooMuchCaseException.class)
    public ResponseEntity<Message> handleTooMuchCaseException(TooMuchCaseException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Message(e.getMessage()));
    }

    @ExceptionHandler(KeywordException.class)
    public ResponseEntity<Message> handleKeywordException(KeywordException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Message(e.getMessage()));
    }

    @ExceptionHandler(UniqueCategoryException.class)
    public ResponseEntity<Message> handleUniqueCategoryException(UniqueCategoryException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message(e.getMessage()));
    }

}
