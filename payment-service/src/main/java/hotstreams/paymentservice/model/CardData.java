package hotstreams.paymentservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardData {
    @NotNull
    private String number;
    @NotNull
    private String expiration;
    @NotNull
    private String cvc;
}
