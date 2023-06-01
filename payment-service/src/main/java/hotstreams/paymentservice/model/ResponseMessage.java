package hotstreams.paymentservice.model;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ResponseMessage {
    public static final ResponseMessage CREATED = new ResponseMessage("Payment was successfully created!");
    public static final ResponseMessage ALREADY_PROCESSED = new ResponseMessage("Payment has been already processed");

    private final String message;
    private String status;
}
