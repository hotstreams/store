CREATE TABLE payments (
    id char(36) PRIMARY KEY NOT NULL,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    number varchar(255) NOT NULL,
    expiration varchar(255) NOT NULL,
    cvc varchar(255) NOT NULL,
    order_id char(36) NOT NULL,
    cost bigint NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    version int NOT NULL
);

CREATE TABLE processed_requests (
    id char(36) PRIMARY KEY NOT NULL,
    payment_id char(36) NOT NULL REFERENCES payments,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    version int NOT NULL
);

CREATE TABLE payment_outbox (
    id bigserial PRIMARY KEY NOT NULL,
    payment_id char(36) NOT NULL REFERENCES payments,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    version int NOT NULL
);
