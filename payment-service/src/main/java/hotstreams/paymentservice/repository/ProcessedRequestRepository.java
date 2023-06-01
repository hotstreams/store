package hotstreams.paymentservice.repository;

import hotstreams.paymentservice.entity.ProcessedRequestEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedRequestRepository extends CrudRepository<ProcessedRequestEntity, String> {
}
