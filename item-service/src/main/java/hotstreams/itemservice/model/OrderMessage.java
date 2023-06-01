package hotstreams.itemservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OrderMessage {
    private String id;
    private String orderId;
    private OrderEvent event;
    private String payload;
}
