package hotstreams.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processed_requests")
public class ProcessedRequestEntity extends AbstractEntity {
    @Id
    private String id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentEntity paymentEntity;
}
