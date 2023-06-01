package hotstreams.paymentservice.validator;

import hotstreams.paymentservice.exception.ExceptionMessage;
import hotstreams.paymentservice.exception.PaymentException;
import hotstreams.paymentservice.model.PaymentDto;
import org.springframework.stereotype.Component;
import org.apache.commons.validator.GenericValidator;

import java.util.Objects;

@Component
public class ExpirationValidator implements PaymentValidator {
    private static final String DATE_PATTERN = "MM/yy";

    @Override
    public void validate(final PaymentDto paymentDto) {
        Objects.requireNonNull(paymentDto);
        Objects.requireNonNull(paymentDto.getCardData());
        Objects.requireNonNull(paymentDto.getCardData().getExpiration());

        final String expiration = paymentDto.getCardData().getExpiration();
        if (!GenericValidator.isDate(expiration, DATE_PATTERN, true)) {
            throw new PaymentException(ExceptionMessage.INVALID_DATE.getMessage());
        }
    }
}
