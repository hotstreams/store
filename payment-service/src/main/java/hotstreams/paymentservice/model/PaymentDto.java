package hotstreams.paymentservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentDto {
    @Valid
    @NotNull
    private CardHolder cardHolder;
    @Valid
    @NotNull
    private CardData cardData;
    @NotNull
    private String orderId;
    @NotNull
    private BigDecimal cost;
}
