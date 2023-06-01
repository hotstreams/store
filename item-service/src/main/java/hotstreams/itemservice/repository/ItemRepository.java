package hotstreams.itemservice.repository;

import hotstreams.itemservice.entity.ItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<ItemEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Flux<ItemEntity> findAllById(Iterable<Long> longs);
}
