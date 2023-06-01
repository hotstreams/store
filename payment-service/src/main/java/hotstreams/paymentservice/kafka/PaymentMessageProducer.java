package hotstreams.paymentservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.paymentservice.conf.KafkaPropertiesConfig;
import hotstreams.paymentservice.entity.PaymentOutboxEntity;
import hotstreams.paymentservice.model.OrderEvent;
import hotstreams.paymentservice.model.OrderMessage;
import hotstreams.paymentservice.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageProducer {
    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaPropertiesConfig kafkaPropertiesConfig;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void producePaymentMessage() {
        log.info("producing payment messages");
        List<PaymentOutboxEntity> outboxEntityList = outboxRepository.findAll();
        outboxEntityList.forEach(entity -> {
            final OrderMessage message = OrderMessage.builder()
                .id(UUID.randomUUID().toString())
                .orderId(entity.getPayment().getOrderId())
                .event(OrderEvent.PAYMENT_ACCEPTED)
                .build();

            log.info("sending " + message);

            kafkaTemplate.send(buildRecord(message));
            outboxRepository.deleteById(entity.getId());
        });
    }

    @SneakyThrows
    private ProducerRecord<String, String> buildRecord(final OrderMessage message) {
        return new ProducerRecord<>(kafkaPropertiesConfig.getTopic(),
                message.getOrderId(),
                objectMapper.writeValueAsString(message));
    }
}
