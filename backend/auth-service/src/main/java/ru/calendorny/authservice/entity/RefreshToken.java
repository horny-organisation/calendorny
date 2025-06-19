package ru.calendorny.authservice.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RefreshToken {

    private final String token;
    private final UUID userId;
    private Instant createdAt;

    public RefreshToken(String token, UUID userId) {
        this.token = token;
        this.userId = userId;
    }

    public RefreshToken(String token, UUID userId, Instant createdAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
