package ru.calendorny.zoommeetingservice.dto.oauth;

import java.time.Instant;

public record OAuth2AuthorizedClientDTO(
    String clientRegistrationId,
    String principalName,
    String encryptedAccessTokenValue,
    Instant accessTokenIssuedAt,
    Instant accessTokenExpiresAt,
    String encryptedRefreshTokenValue,
    Instant refreshTokenIssuedAt
) {
}
