package hotstreams.orderservice.model;

public enum OrderStatus {
    CREATED,
    ITEM_PENDING,
    PAYMENT_PENDING,
    CONFIRMED,
    REJECTED,
    CANCELED
}
