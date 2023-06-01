package hotstreams.paymentservice.exception;

public class InvalidRequestIdException extends RuntimeException {
    public InvalidRequestIdException(String message) {
        super(message);
    }
}
