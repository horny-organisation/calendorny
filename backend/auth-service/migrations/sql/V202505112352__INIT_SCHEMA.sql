--liquibase formatted sql

CREATE TABLE accounts
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT                NOT NULL,
    created_at    TIMESTAMP                    DEFAULT CURRENT_TIMESTAMP,
    is_active     BOOLEAN             NOT NULL DEFAULT TRUE
);


COMMENT ON COLUMN accounts.created_at IS 'Do not write about commenting columns in MR :P';

CREATE TABLE profiles
(
    user_id      UUID PRIMARY KEY REFERENCES accounts (id),
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    birth_date   DATE,
    phone_number VARCHAR(20),
    telegram     VARCHAR(100),
    timezone     VARCHAR(50),
    language     VARCHAR(10)
);

