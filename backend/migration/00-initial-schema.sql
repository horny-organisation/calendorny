CREATE TABLE oauth2_authorized_client
(
    client_registration_id  VARCHAR(100) NOT NULL,
    principal_name          VARCHAR(200) NOT NULL,
    access_token_type       VARCHAR(100),
    access_token_value      BYTEA,
    access_token_issued_at  TIMESTAMP,
    access_token_expires_at TIMESTAMP,
    access_token_scopes     TEXT,
    refresh_token_value     BYTEA,
    refresh_token_issued_at TIMESTAMP,
    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (client_registration_id, principal_name)
);
