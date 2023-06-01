package hotstreams.orderservice.kafka;

import hotstreams.orderservice.entity.OrderOutboxEntity;
import hotstreams.orderservice.model.OrderMessage;
import hotstreams.orderservice.model.OrderStatus;
import hotstreams.orderservice.exceptions.OrderNotFoundException;
import hotstreams.orderservice.entity.OrderEntity;
import hotstreams.orderservice.model.OrderEvent;
import hotstreams.orderservice.repository.OrderOutboxRepository;
import hotstreams.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMessageProcessor {
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository outboxRepository;

    @Transactional
    public void processOrderMessage(final OrderMessage message) {
        switch (message.getEvent()) {
            case ITEMS_RESERVED -> processItemReservedEvent(message);
            case ITEMS_OUT_OF_STOCK -> processItemOutOfStockEvent(message);
            case PAYMENT_ACCEPTED -> processPaymentAcceptedEvent(message);
            case PAYMENT_REJECTED -> processPaymentRejectedEvent(message);
        }
    }

    public void processItemReservedEvent(final OrderMessage message) {
        OrderEntity order = orderRepository.findById(message.getOrderId()).orElseThrow(OrderNotFoundException::new);
        order.setStatus(OrderStatus.PAYMENT_PENDING);
    }

    public void processItemOutOfStockEvent(final OrderMessage message) {
        OrderEntity order = orderRepository.findById(message.getOrderId()).orElseThrow(OrderNotFoundException::new);
        order.setStatus(OrderStatus.REJECTED);
        outboxRepository.save(OrderOutboxEntity.fromOrderEntity(order, OrderEvent.REJECTED));
    }

    public void processPaymentAcceptedEvent(final OrderMessage message) {
        OrderEntity order = orderRepository.findById(message.getOrderId()).orElseThrow(OrderNotFoundException::new);
        order.setStatus(OrderStatus.CONFIRMED);
        outboxRepository.save(OrderOutboxEntity.fromOrderEntity(order, OrderEvent.CONFIRMED));
    }

    public void processPaymentRejectedEvent(final OrderMessage message) {
        OrderEntity order = orderRepository.findById(message.getOrderId()).orElseThrow(OrderNotFoundException::new);
        order.setStatus(OrderStatus.REJECTED);
        outboxRepository.save(OrderOutboxEntity.fromOrderEntity(order, OrderEvent.REJECTED));
    }
}
