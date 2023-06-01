package hotstreams.authservice.exception;

import hotstreams.authservice.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> uniqueConstraintExceptionHandler(final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        final ApiResponse responseMessage = new ApiResponse("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMessage);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> uniqueConstraintExceptionHandler(final EmailAlreadyExistsException ex) {
        log.error(ex.getMessage());
        final ApiResponse responseMessage = new ApiResponse("error", ex.getMessage());
        return ResponseEntity.badRequest().body(responseMessage);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiResponse handleBadCredentialsException(BadCredentialsException ex) {
        log.error(ex.getMessage());
        return new ApiResponse("error", ex.getMessage());
    }
}
