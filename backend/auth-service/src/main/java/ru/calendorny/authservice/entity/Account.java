package ru.calendorny.authservice.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Account {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private Instant createdAt;
    private boolean isActive;
    private Profile profile;

    public Account(UUID id, String email, String passwordHash) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public Account(UUID id, String email, String passwordHash, Instant createdAt, boolean isActive) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public Account(Account account, Profile profile) {
        this.id = account.id;
        this.email = account.email;
        this.passwordHash = account.passwordHash;
        this.createdAt = account.createdAt;
        this.isActive = account.isActive;
        this.profile = profile;
    }
}
