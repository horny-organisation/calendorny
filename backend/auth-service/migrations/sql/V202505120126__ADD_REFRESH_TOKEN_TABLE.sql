--liquibase formatted sql

--changeset shcherbanev:202505120126-add-refresh-token-table
CREATE TABLE refresh_tokens
(
    token      TEXT PRIMARY KEY,
    user_id    UUID REFERENCES accounts (id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
