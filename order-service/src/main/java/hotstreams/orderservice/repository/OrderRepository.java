package hotstreams.orderservice.repository;

import hotstreams.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findOrderEntitiesByCustomerId(String customerId);
    Optional<OrderEntity> findOrderEntityByCustomerIdAndId(String customerId, String id);
}
