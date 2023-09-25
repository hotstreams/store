package hotstreams.orderservice;

import hotstreams.orderservice.configuration.AuditingConfiguration;
import hotstreams.orderservice.entity.OrderEntity;
import hotstreams.orderservice.exceptions.OrderNotFoundException;
import hotstreams.orderservice.kafka.OrderMessageProcessor;
import hotstreams.orderservice.model.OrderEvent;
import hotstreams.orderservice.model.OrderMessage;
import hotstreams.orderservice.model.OrderStatus;
import hotstreams.orderservice.repository.OrderOutboxRepository;
import hotstreams.orderservice.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AuditingConfiguration.class)
@RunWith(SpringRunner.class)
public class OrderMessageProcessorTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("tests");

    static {
        postgres.start();
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public OrderMessageProcessor orderMessageProcessor(OrderRepository orderRepository,
                                                           OrderOutboxRepository orderOutboxRepository) {
            return new OrderMessageProcessor(orderRepository, orderOutboxRepository);
        }
    }

    @DynamicPropertySource
    private static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMessageProcessor messageProcessor;

    @Test(expected = OrderNotFoundException.class)
    public void shouldNotFindOrderWhenDoesNotExist() {
        final OrderMessage orderMessage = OrderMessage.builder()
                        .id(UUID.randomUUID().toString())
                        .orderId(UUID.randomUUID().toString())
                        .event(OrderEvent.ITEMS_RESERVED)
                        .build();
        messageProcessor.processOrderMessage(orderMessage);
    }

    @Test
    public void shouldUpdateStatusOrderToPaymentPending() {
        OrderEntity orderEntity = OrderEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .customerId(UUID.randomUUID().toString())
                        .status(OrderStatus.CREATED)
                        .itemsId(List.of(1L, 2L, 3L))
                        .totalCost(100)
                        .build();
        orderRepository.save(orderEntity);

        final OrderMessage orderMessage = OrderMessage.builder()
                .id(UUID.randomUUID().toString())
                .orderId(orderEntity.getId())
                .event(OrderEvent.ITEMS_RESERVED)
                .build();

        messageProcessor.processOrderMessage(orderMessage);

        OrderEntity order = orderRepository.findById(orderMessage.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.PAYMENT_PENDING, order.getStatus());
    }
}
