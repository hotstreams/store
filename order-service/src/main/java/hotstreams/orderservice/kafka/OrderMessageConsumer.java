package hotstreams.orderservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.orderservice.model.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMessageConsumer {
    private final OrderMessageProcessor messageProcessor;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic}")
    public void messageConsumer(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Consuming message " + record);
        final OrderMessage orderMessage = readOrderMessage(record.value());
        messageProcessor.processOrderMessage(orderMessage);
        ack.acknowledge();
    }

    @SneakyThrows
    private OrderMessage readOrderMessage(final String payload) {
        return objectMapper.readValue(payload, OrderMessage.class);
    }
}
