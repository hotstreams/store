package hotstreams.itemservice.repository;

import hotstreams.itemservice.entity.ItemOutboxEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ItemOutboxRepository extends ReactiveCrudRepository<ItemOutboxEntity, Long> {
    Mono<ItemOutboxEntity> findTopByOrderByIdDesc();
}

