package hotstreams.itemservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.itemservice.configuration.AuditingConfiguration;
import hotstreams.itemservice.entity.ItemEntity;
import hotstreams.itemservice.model.OrderDto;
import hotstreams.itemservice.model.OrderEvent;
import hotstreams.itemservice.model.OrderMessage;
import hotstreams.itemservice.repository.ItemOutboxRepository;
import hotstreams.itemservice.repository.ItemRepository;
import hotstreams.itemservice.service.ItemService;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Testcontainers
@DataR2dbcTest
@ComponentScan(basePackageClasses = {ItemService.class})
@RunWith(SpringRunner.class)
public class ItemServiceTest {
    @TestConfiguration
    @Import(AuditingConfiguration.class)
    public static class Configuration {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("tests");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    private static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> postgres.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemOutboxRepository itemOutboxRepository;

    @Autowired
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldDoNotChangeStockAndProduceOutOfStock() {
        Flux<ItemEntity> itemEntities =
                itemRepository.saveAll(createItemListWithCount(List.of(5, 4, 0)))
                        .collectList()
                        .flatMapMany(persisted ->
                                itemService.reserve(createOrderMessage(persisted))
                                        .thenMany(itemRepository.findAllById(persisted.stream().map(ItemEntity::getId).toList()))
                        );

        StepVerifier.create(itemEntities)
                .expectNextMatches(entity -> entity.getCount() == 5)
                .expectNextMatches(entity -> entity.getCount() == 4)
                .expectNextMatches(entity -> entity.getCount() == 0)
                .verifyComplete();

        StepVerifier.create(itemOutboxRepository.findTopByOrderByIdDesc())
                .expectNextMatches(entity -> entity.getOrderEvent() == OrderEvent.ITEMS_OUT_OF_STOCK)
                .verifyComplete();
    }

    @Test
    public void shouldReserveItemsAndProduceItemsReserved() {
        Flux<ItemEntity> itemEntities =
                itemRepository.saveAll(createItemListWithCount(List.of(5, 4, 3)))
                .collectList()
                .flatMapMany(persisted ->
                    itemService.reserve(createOrderMessage(persisted))
                            .thenMany(itemRepository.findAllById(persisted.stream().map(ItemEntity::getId).toList()))
                );

        StepVerifier.create(itemEntities)
                .expectNextMatches(entity -> entity.getCount() == 4)
                .expectNextMatches(entity -> entity.getCount() == 3)
                .expectNextMatches(entity -> entity.getCount() == 2)
                .verifyComplete();

        StepVerifier.create(itemOutboxRepository.findTopByOrderByIdDesc())
                .expectNextMatches(entity -> entity.getOrderEvent() == OrderEvent.ITEMS_RESERVED)
                .verifyComplete();
    }

    @SneakyThrows
    private String createPayloadFor(List<ItemEntity> items) {
        final OrderDto orderDto = OrderDto.builder()
                .customerId(UUID.randomUUID().toString())
                .itemIds(items.stream().map(ItemEntity::getId).toList())
                .totalCost(100)
                .build();

        return objectMapper.writeValueAsString(orderDto);
    }

    private OrderMessage createOrderMessage(List<ItemEntity> items) {
        return OrderMessage.builder()
                .id(UUID.randomUUID().toString())
                .orderId(UUID.randomUUID().toString())
                .event(OrderEvent.CREATED)
                .payload(createPayloadFor(items))
                .build();
    }

    private List<ItemEntity> createItemListWithCount(List<Integer> countList) {
        List<ItemEntity> entities = new ArrayList<>();
        countList.forEach(count -> {
            entities.add(ItemEntity.builder()
                    .name("item")
                    .description("item")
                    .cost(100)
                    .count(count)
                    .build());
        });
        return entities;
    }
}
