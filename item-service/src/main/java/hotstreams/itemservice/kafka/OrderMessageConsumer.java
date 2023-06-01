package hotstreams.itemservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderMessageConsumer implements CommandLineRunner {
    private final OrderMessageProcessor messageProcessor;
    private final KafkaReceiver<String, String> kafkaReceiver;

    public Disposable consume() {
        log.debug("listening to kafka");
        return kafkaReceiver.receive()
                .doOnNext(record -> log.debug("consuming " + record))
                .flatMap(record -> messageProcessor.processMessage(record.value())
                        .doOnSuccess(res -> {
                            ReceiverOffset offset = record.receiverOffset();
                            log.info("Successfully handled msg with id={}", record.key());
                            offset.acknowledge();
                        })
                        .doOnError(ex -> log.error("Exception {} happened while processing msg with id = {}", ex, record.key()))
                )
                .subscribe();
    }

    @Override
    public void run(String... args) {
        consume();
    }
}
