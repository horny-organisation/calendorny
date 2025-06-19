--liquibase formatted sql

CREATE TABLE refresh_tokens
(
    token      TEXT PRIMARY KEY,
    user_id    UUID REFERENCES accounts (id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
