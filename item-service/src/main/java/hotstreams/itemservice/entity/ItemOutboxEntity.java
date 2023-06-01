package hotstreams.itemservice.entity;

import hotstreams.itemservice.model.OrderEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_outbox")
public class ItemOutboxEntity extends AbstractEntity {
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderEvent orderEvent;

    private String payload;

    public static ItemOutboxEntity from(final OrderEvent event, final String payload) {
        return ItemOutboxEntity.builder()
                .orderEvent(event)
                .payload(payload)
                .build();
    }
}
