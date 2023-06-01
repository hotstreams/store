package hotstreams.orderservice.repository;

import hotstreams.orderservice.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutboxEntity, Long> {
}
