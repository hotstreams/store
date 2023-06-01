CREATE TABLE orders (
    id char(36) PRIMARY KEY NOT NULL,
    customer_id char(36) NOT NULL,
    total_cost integer NOT NULL,
    status varchar(255) NOT NULL,
    version integer,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);

CREATE TABLE order_items_id (
    order_id char(36) NOT NULL REFERENCES orders,
    item_id bigint NOT NULL
);

CREATE TABLE order_outbox (
    id bigserial PRIMARY KEY NOT NULL,
    order_id char(36) NOT NULL,
    event varchar(255) NOT NULL,
    version integer,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);
