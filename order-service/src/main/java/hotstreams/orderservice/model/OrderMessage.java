package hotstreams.orderservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.orderservice.entity.OrderEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OrderMessage {
    private String id;
    private String orderId;
    private OrderEvent event;
    private String payload;
}
