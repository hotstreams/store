package hotstreams.paymentservice.repository;

import hotstreams.paymentservice.entity.PaymentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends CrudRepository<PaymentEntity, String> {
}
