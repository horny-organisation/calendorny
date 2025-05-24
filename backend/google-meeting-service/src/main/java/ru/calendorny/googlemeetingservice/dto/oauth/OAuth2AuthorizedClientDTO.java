package ru.calendorny.googlemeetingservice.dto.oauth;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import ru.calendorny.googlemeetingservice.service.AesEncryptionService;

@Getter
@Setter
@NoArgsConstructor
public class OAuth2AuthorizedClientDTO {

    private String clientRegistrationId;
    private String principalName;

    private String accessTokenValue;
    private Instant accessTokenIssuedAt;
    private Instant accessTokenExpiresAt;

    private String refreshTokenValue;
    private Instant refreshTokenIssuedAt;

    public OAuth2AuthorizedClientDTO(OAuth2AuthorizedClient client, AesEncryptionService encryptionService) {
        this.clientRegistrationId = client.getClientRegistration().getRegistrationId();
        this.principalName = client.getPrincipalName();
        this.accessTokenValue = encrypt(client.getAccessToken().getTokenValue(), encryptionService);
        this.accessTokenIssuedAt = client.getAccessToken().getIssuedAt();
        this.accessTokenExpiresAt = client.getAccessToken().getExpiresAt();

        if (client.getRefreshToken() != null) {
            this.refreshTokenValue = encrypt(client.getRefreshToken().getTokenValue(), encryptionService);
            this.refreshTokenIssuedAt = client.getRefreshToken().getIssuedAt();
        }
    }

    private static String encrypt(String plaintext, AesEncryptionService encryptionService) {
        return (plaintext != null) ? encryptionService.encrypt(plaintext) : null;
    }

    private String decrypt(String ciphertext, AesEncryptionService encryptionService) {
        return (ciphertext != null) ? encryptionService.decrypt(ciphertext) : null;
    }

    public String getAccessTokenValue(AesEncryptionService encryptionService) {
        return decrypt(accessTokenValue, encryptionService);
    }

    public String getRefreshTokenValue(AesEncryptionService encryptionService) {
        return decrypt(refreshTokenValue, encryptionService);
    }
}
