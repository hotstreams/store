package hotstreams.paymentservice.service;

import hotstreams.paymentservice.model.PaymentDto;

public interface PaymentService {
    String createPayment(final PaymentDto paymentDto, final String requestId);
    boolean isProcessed(final String id);
}
