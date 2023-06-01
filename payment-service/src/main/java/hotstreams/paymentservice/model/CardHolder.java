package hotstreams.paymentservice.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardHolder {
    @NotNull
    private String name;
    @NotNull
    private String email;
}
