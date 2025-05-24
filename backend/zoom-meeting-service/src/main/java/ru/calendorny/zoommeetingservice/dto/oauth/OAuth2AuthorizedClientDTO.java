package ru.calendorny.zoommeetingservice.dto.oauth;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import ru.calendorny.zoommeetingservice.service.AesEncryptionService;

@Getter
@Setter
public class OAuth2AuthorizedClientDTO {

    @Setter
    private static AesEncryptionService encryptionService;

    private String clientRegistrationId;
    private String principalName;

    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;

    public OAuth2AuthorizedClientDTO(OAuth2AuthorizedClient client) {
        this.clientRegistrationId = client.getClientRegistration().getRegistrationId();
        this.principalName = client.getPrincipalName();
        this.accessTokenValue = encrypt(client.getAccessToken().getTokenValue());
        this.accessTokenIssuedAt = client.getAccessToken().getIssuedAt();
        this.accessTokenExpiresAt = client.getAccessToken().getExpiresAt();

        if (client.getRefreshToken() != null) {
            this.refreshTokenValue = encrypt(client.getRefreshToken().getTokenValue());
            this.refreshTokenIssuedAt = client.getRefreshToken().getIssuedAt();
        }
    }

    private static String encrypt(String plaintext) {
        return (plaintext != null && encryptionService != null) ? encryptionService.encrypt(plaintext) : plaintext;
    }

    private static String decrypt(String ciphertext) {
        return (ciphertext != null && encryptionService != null) ? encryptionService.decrypt(ciphertext) : ciphertext;
    }

    public String getAccessTokenValue() {
        return decrypt(accessTokenValue);
    }

    public String getRefreshTokenValue() {
        return decrypt(refreshTokenValue);
    }
}
