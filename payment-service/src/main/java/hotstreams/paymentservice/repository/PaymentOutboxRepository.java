package hotstreams.paymentservice.repository;

import hotstreams.paymentservice.entity.PaymentOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOutboxRepository extends JpaRepository<PaymentOutboxEntity, Long> {
}
