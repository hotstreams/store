package hotstreams.paymentservice.controller;

import hotstreams.paymentservice.filter.RequestIdFilter;
import hotstreams.paymentservice.model.PaymentDto;
import hotstreams.paymentservice.model.ResponseMessage;
import hotstreams.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> createPayment(@Valid @RequestBody PaymentDto paymentDto,
                                                         @RequestHeader(RequestIdFilter.REQUEST_ID_HEADER) String requestId) {
        if (paymentService.isProcessed(requestId)) {
            return ResponseEntity.badRequest().body(ResponseMessage.ALREADY_PROCESSED);
        }

        paymentService.createPayment(paymentDto, requestId);
        return ResponseEntity.ok(ResponseMessage.CREATED);
    }
}
