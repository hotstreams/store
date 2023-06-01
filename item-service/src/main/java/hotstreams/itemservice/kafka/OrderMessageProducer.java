package hotstreams.itemservice.kafka;

import hotstreams.itemservice.configuration.KafkaProperties;
import hotstreams.itemservice.entity.ItemOutboxEntity;
import hotstreams.itemservice.repository.ItemOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderMessageProducer {
    private final KafkaSender<String, String> kafkaSender;
    private final KafkaProperties kafkaProperties;
    private final ItemOutboxRepository outboxRepository;
    private final TransactionalOperator transactionalOperator;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void producer() {
        log.info("producing outbox messages");

        transactionalOperator.transactional(
            outboxRepository.findAll()
                .flatMap(this::produceMessage)
                .flatMap(outboxRepository::delete)
        )
        .subscribe();
    }

    public Mono<ItemOutboxEntity> produceMessage(final ItemOutboxEntity entity) {
        return kafkaSender.send(
                Mono.just(entity)
                    .map(this::buildRecord)
                    .map(record -> SenderRecord.create(record, entity.getId().toString()))
                )
                .doOnNext(id -> log.info("Message sent with id {}", id))
                .then(Mono.just(entity));
    }

    @SneakyThrows
    private ProducerRecord<String, String> buildRecord(final ItemOutboxEntity message) {
        return new ProducerRecord<>(kafkaProperties.getTopic(), message.getId().toString(), message.getPayload());
    }
}
