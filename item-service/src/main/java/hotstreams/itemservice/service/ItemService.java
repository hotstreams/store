package hotstreams.itemservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import hotstreams.itemservice.entity.ItemEntity;
import hotstreams.itemservice.entity.ItemOutboxEntity;
import hotstreams.itemservice.exceptions.ItemNotFoundException;
import hotstreams.itemservice.exceptions.ItemOutOfStockException;
import hotstreams.itemservice.model.*;
import hotstreams.itemservice.repository.ItemOutboxRepository;
import hotstreams.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository repository;
    private final ItemOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Mono<Void> reserve(final OrderMessage message) {
        log.info("reserving message " + message);
        return repository.findAllById(getItemsFromMessage(message))
                .collectList()
                .doOnNext(list -> log.info("processing entities = " + list))
                .map(list -> validateItemsExist(list, getItemsFromMessage(message)))
                .map(this::reserveStock)
                .flatMapMany(repository::saveAll)
                .then(outboxRepository.save(createItemOutboxEntity(message, OrderEvent.ITEMS_RESERVED)))
                .onErrorResume(Exception.class, ex -> outboxRepository.save(createItemOutboxEntity(message, OrderEvent.ITEMS_OUT_OF_STOCK)))
                .then();
    }

    @Transactional
    public Mono<Void> cancel(final OrderMessage message) {
        return repository.findAllById(getItemsFromMessage(message))
                .doOnNext(item -> item.setCount(item.getCount() + 1))
                .map(repository::save)
                .then();
    }

    private List<ItemEntity> reserveStock(final List<ItemEntity> itemList) {
        if (itemList.stream().anyMatch(item -> item.getCount() < 1)) {
            throw new ItemOutOfStockException();
        }
        for (ItemEntity item : itemList) {
            item.setCount(item.getCount() - 1);
        }
        return itemList;
    }

    @SneakyThrows
    private List<Long> getItemsFromMessage(final OrderMessage message) {
        final OrderDto orderDto = objectMapper.readValue(message.getPayload(), OrderDto.class);
        log.info("got orderDto = " + orderDto);
        return orderDto.getItemIds();
    }

    @SneakyThrows
    private ItemOutboxEntity createItemOutboxEntity(final OrderMessage orderMessage, final OrderEvent event) {
        orderMessage.setId(UUID.randomUUID().toString());
        orderMessage.setEvent(event);
        return ItemOutboxEntity.from(event, objectMapper.writeValueAsString(orderMessage));
    }

    private List<ItemEntity> validateItemsExist(final List<ItemEntity> itemList, final List<Long> itemIds) {
        if (itemList == null || itemIds == null || itemList.size() != itemIds.size()) {
            throw new ItemNotFoundException();
        }
        return itemList;
    }
}
