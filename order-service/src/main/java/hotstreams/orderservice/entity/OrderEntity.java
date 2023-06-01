package hotstreams.orderservice.entity;

import hotstreams.orderservice.model.OrderDto;
import hotstreams.orderservice.model.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity extends AbstractEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    private String customerId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items_id", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "item_id")
    private List<Long> itemsId;
    private Integer totalCost;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public static OrderEntity newOrder(OrderDto orderDto) {
        OrderEntity order = new OrderEntity();
        order.setId(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.CREATED);
        order.setItemsId(orderDto.getItemIds());
        order.setTotalCost(orderDto.getTotalCost());
        return order;
    }

    public OrderDto toDto() {
        return new OrderDto(itemsId, totalCost);
    }
}
