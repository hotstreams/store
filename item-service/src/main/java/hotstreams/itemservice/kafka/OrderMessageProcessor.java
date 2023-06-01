package hotstreams.itemservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.itemservice.model.OrderEvent;
import hotstreams.itemservice.model.OrderMessage;
import hotstreams.itemservice.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMessageProcessor {
    private final ObjectMapper objectMapper;
    private final ItemService itemService;

    public Mono<Void> processMessage(final String message) {
        return Mono.just(message)
                .map(this::readMessage)
                .filter(this::isValid)
                .filter(this::shouldDispatch)
                .flatMap(this::dispatchMessage);
    }

    @SneakyThrows
    private OrderMessage readMessage(final String message) {
        log.info("reading message");
        return objectMapper.readValue(message, OrderMessage.class);
    }

    private boolean isValid(final OrderMessage message) {
        log.info("validating message");
        return Objects.nonNull(message.getId()) && Objects.nonNull(message.getOrderId()) &&
            Objects.nonNull(message.getEvent()) && Objects.nonNull(message.getPayload());
    }

    private boolean shouldDispatch(final OrderMessage message) {
        log.info("validating dispatch");
        return message.getEvent() == OrderEvent.CREATED || message.getEvent() == OrderEvent.CANCELED;
    }

    private Mono<Void> dispatchMessage(final OrderMessage message) {
        log.info("dispatching messages");
        return switch (message.getEvent()) {
            case CREATED -> itemService.reserve(message);
            case CANCELED -> itemService.cancel(message);
            default -> Mono.empty();
        };
    }
}
