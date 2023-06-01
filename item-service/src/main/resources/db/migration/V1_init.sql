CREATE TABLE items (
    id bigserial PRIMARY KEY NOT NULL,
    name varchar(255) NOT NULL,
    description text NOT NULL,
    cost int NOT NULL,
    count int NOT NULL,
    version integer NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);

CREATE TABLE item_outbox (
    id bigserial PRIMARY KEY NOT NULL,
    payload text,
    order_event varchar(255) NOT NULL,
    version integer NOT NULL,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);
