package hotstreams.itemservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    @NotNull
    private String customerId;
    @NotNull
    @Size(min = 1)
    private List<Long> itemIds;
    @Min(0)
    private Integer totalCost;
}
