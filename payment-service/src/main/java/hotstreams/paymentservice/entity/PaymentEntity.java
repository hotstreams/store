package hotstreams.paymentservice.entity;

import hotstreams.paymentservice.model.CardData;
import hotstreams.paymentservice.model.CardHolder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentEntity extends AbstractEntity {
    @Id
    private String id;
    @Embedded
    private CardHolder cardHolder;
    @Embedded
    private CardData cardData;
    private String orderId;
    private BigDecimal cost;
}
