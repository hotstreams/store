package hotstreams.orderservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.orderservice.configuration.KafkaPropertiesConfig;
import hotstreams.orderservice.entity.OrderEntity;
import hotstreams.orderservice.entity.OrderOutboxEntity;
import hotstreams.orderservice.model.OrderEvent;
import hotstreams.orderservice.model.OrderMessage;
import hotstreams.orderservice.model.OrderStatus;
import hotstreams.orderservice.repository.OrderOutboxRepository;
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
public class OrderMessageProducer {
    private final OrderOutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;
    private final KafkaPropertiesConfig kafkaPropertiesConfig;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void producer() {
        log.debug("producing messages");
        List<OrderOutboxEntity> orderOutboxList = repository.findAll();
        orderOutboxList.forEach(message -> {
            log.debug("sending " + message);
            kafkaTemplate.send(buildRecord(OrderMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(message.getOrder().getId())
                    .event(message.getEvent())
                    .payload(map(message.getOrder()))
                    .build()));
            if (message.getEvent() == OrderEvent.CREATED) {
                message.getOrder().setStatus(OrderStatus.ITEM_PENDING);
            }
            repository.deleteById(message.getId());
        });
    }

    @SneakyThrows
    private String map(final OrderEntity orderEntity) {
        return mapper.writeValueAsString(orderEntity.toDto());
    }

    @SneakyThrows
    private ProducerRecord<String, String> buildRecord(final OrderMessage message) {
        return new ProducerRecord<>(kafkaPropertiesConfig.getTopic(), message.getOrderId(), mapper.writeValueAsString(message));
    }
}
