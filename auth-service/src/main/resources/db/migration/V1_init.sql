CREATE TABLE users (
    id char(36) PRIMARY KEY,
    username varchar(255) NOT NULL CHECK (char_length(username) >= 8),
    password varchar(255) NOT NULL,
    email varchar(64) NOT NULL UNIQUE
);

CREATE TABLE devices (
    id serial PRIMARY KEY,
    device_id varchar(36) NOT NULL,
    user_id char(36) NOT NULL REFERENCES users(id)
);

CREATE TABLE refresh_tokens (
    id serial PRIMARY KEY,
    token text NOT NULL UNIQUE,
    expiration_at TIMESTAMP,
    device_id bigint REFERENCES devices(id)
);

CREATE TABLE roles (
    id serial PRIMARY KEY,
    role_name text
);

CREATE TABLE user_role (
    user_id char(36) REFERENCES users(id),
    role_id bigint REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
