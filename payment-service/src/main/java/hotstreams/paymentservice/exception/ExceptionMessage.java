package hotstreams.paymentservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    INVALID_DATE("Invalid date format!"),
    INVALID_NUMBER("Invalid card number!"),
    INVALID_REQUEST_ID("Invalid request id!");

    private final String message;
}
