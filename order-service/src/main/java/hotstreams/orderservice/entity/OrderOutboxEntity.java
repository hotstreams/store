package hotstreams.orderservice.entity;

import hotstreams.orderservice.model.OrderEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_outbox")
public class OrderOutboxEntity extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderEvent event;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    public static OrderOutboxEntity fromOrderEntity(final OrderEntity orderEntity, final OrderEvent orderEvent) {
        return OrderOutboxEntity.builder()
                .event(orderEvent)
                .order(orderEntity)
                .build();
    }
}
