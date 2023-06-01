package hotstreams.orderservice.exceptions;

import hotstreams.orderservice.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class OrderExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> constraintViolationHandler(final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        final ApiResponse apiResponse = new ApiResponse("error", "Bad request parameters");
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse> orderNotFoundHandler(final OrderNotFoundException ex) {
        log.error(ex.getMessage());
        final ApiResponse apiResponse = new ApiResponse("error", "Order not found");
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
