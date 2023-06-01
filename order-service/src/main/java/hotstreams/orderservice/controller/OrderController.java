package hotstreams.orderservice.controller;

import hotstreams.orderservice.model.*;
import hotstreams.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders() {
        log.trace("getting orders");
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).noTransform().mustRevalidate())
                .body(orderService.getOrders());

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDto> getStatusForOrder(@PathVariable("id") String orderId) {
        final OrderDto orderDto = orderService.getOrder(orderId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderDto orderDto) {
        log.debug("got order for creation: " + orderDto);
        final String id = orderService.createOrder(orderDto);
        return ResponseEntity.ok(new ApiResponse("ok", "Order has been created with id " + id));
    }
}
