package hotstreams.orderservice.service;

import hotstreams.orderservice.entity.OrderOutboxEntity;
import hotstreams.orderservice.exceptions.OrderNotFoundException;
import hotstreams.orderservice.model.OrderDto;
import hotstreams.orderservice.entity.OrderEntity;
import hotstreams.orderservice.model.OrderEvent;
import hotstreams.orderservice.repository.OrderOutboxRepository;
import hotstreams.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    public String createOrder(OrderDto orderDto) {
        OrderEntity order = OrderEntity.newOrder(orderDto);
        order.setCustomerId(getCurrentCustomerId());
        orderRepository.save(order);
        orderOutboxRepository.save(OrderOutboxEntity.fromOrderEntity(order, OrderEvent.CREATED));
        return order.getId();
    }

    @Transactional(readOnly = true)
    public OrderDto getOrder(final String orderId) {
        return orderRepository.findOrderEntityByCustomerIdAndId(getCurrentCustomerId(), orderId).orElseThrow(OrderNotFoundException::new).toDto();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepository.findOrderEntitiesByCustomerId(getCurrentCustomerId()).stream().map(OrderEntity::toDto).toList();
    }

    private String getCurrentCustomerId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
