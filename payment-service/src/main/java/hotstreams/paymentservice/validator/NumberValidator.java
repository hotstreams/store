package hotstreams.paymentservice.validator;

import hotstreams.paymentservice.exception.ExceptionMessage;
import hotstreams.paymentservice.exception.PaymentException;
import hotstreams.paymentservice.model.PaymentDto;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NumberValidator implements PaymentValidator {
    @Override
    public void validate(final PaymentDto paymentDto) {
        Objects.requireNonNull(paymentDto.getCardData());
        Objects.requireNonNull(paymentDto.getCardData().getNumber());

        if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(paymentDto.getCardData().getNumber())) {
            throw new PaymentException(ExceptionMessage.INVALID_NUMBER.getMessage());
        }
    }
}
