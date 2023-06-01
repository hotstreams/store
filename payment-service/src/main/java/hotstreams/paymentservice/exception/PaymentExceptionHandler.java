package hotstreams.paymentservice.exception;

import hotstreams.paymentservice.model.ResponseMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class PaymentExceptionHandler {
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ResponseMessage> paymentExceptionHandler(final PaymentException ex) {
        log.error(ex.getMessage());
        final ResponseMessage responseMessage = new ResponseMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(responseMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseMessage> uniqueConstraintExceptionHandler(final ConstraintViolationException ex) {
        log.error(ex.getMessage());
        final ResponseMessage responseMessage = new ResponseMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(responseMessage);
    }

    @ExceptionHandler(InvalidRequestIdException.class)
    public ResponseEntity<ResponseMessage> invalidRequestIdExceptionHandler(final InvalidRequestIdException ex) {
        log.error(ex.getMessage());
        final ResponseMessage responseMessage = new ResponseMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(responseMessage);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResponseMessage> invalidRequestIdExceptionHandler(final DuplicateKeyException ex) {
        log.error(ex.getMessage());
        //TODO: make sure its processed request entity
        return ResponseEntity.badRequest().body(ResponseMessage.ALREADY_PROCESSED);
    }
}
