package ru.calendorny.taskservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.jwt")
public record JwtProperties(
    String privateKey,
    String publicKey,
    int accessTokenExpirationMinutes
) {
}
