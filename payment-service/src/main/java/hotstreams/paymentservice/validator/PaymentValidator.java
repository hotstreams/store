package hotstreams.paymentservice.validator;

import hotstreams.paymentservice.model.PaymentDto;

public interface PaymentValidator {
    void validate(final PaymentDto paymentDto);
}
