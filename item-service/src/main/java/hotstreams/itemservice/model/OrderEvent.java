package hotstreams.itemservice.model;

public enum OrderEvent {
    CREATED,
    ITEMS_RESERVED,
    ITEMS_OUT_OF_STOCK,
    PAYMENT_ACCEPTED,
    PAYMENT_REJECTED,
    CONFIRMED,
    REJECTED,
    CANCELED
}
