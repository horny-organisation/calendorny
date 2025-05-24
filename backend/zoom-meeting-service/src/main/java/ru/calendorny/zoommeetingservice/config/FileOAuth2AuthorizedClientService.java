package ru.calendorny.zoommeetingservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import ru.calendorny.zoommeetingservice.dto.oauth.OAuth2AuthorizedClientDTO;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;
import ru.calendorny.zoommeetingservice.service.AesEncryptionService;

@Service
public class FileOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private final File storageFile;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, OAuth2AuthorizedClient> clientCache = new ConcurrentHashMap<>();
    private final ClientRegistrationRepository clientRegistrationRepository;

    public FileOAuth2AuthorizedClientService(
            ZoomProperties properties,
            ClientRegistrationRepository clientRegistrationRepository,
            AesEncryptionService encryptionService) {
        this.storageFile = new File(properties.fileName());
        this.clientRegistrationRepository = clientRegistrationRepository;

        OAuth2AuthorizedClientDTO.setEncryptionService(encryptionService);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        loadFromFile();
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(
            String clientRegistrationId, String principalName) {
        String key = key(clientRegistrationId, principalName);
        return (T) clientCache.get(key);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        String key = key(authorizedClient.getClientRegistration().getRegistrationId(), principal.getName());
        clientCache.put(key, authorizedClient);
        saveToFile();
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        clientCache.remove(key(clientRegistrationId, principalName));
        saveToFile();
    }

    private void saveToFile() {
        try {
            Map<String, OAuth2AuthorizedClientDTO> dtoMap = clientCache.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new OAuth2AuthorizedClientDTO(e.getValue())));
            objectMapper.writeValue(storageFile, dtoMap);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save OAuth2 token to file", e);
        }
    }

    private void loadFromFile() {
        if (!storageFile.exists()) return;
        try {
            Map<String, OAuth2AuthorizedClientDTO> dtoMap =
                    objectMapper.readValue(storageFile, new TypeReference<>() {});

            for (Map.Entry<String, OAuth2AuthorizedClientDTO> entry : dtoMap.entrySet()) {
                OAuth2AuthorizedClientDTO dto = entry.getValue();
                ClientRegistration registration =
                        clientRegistrationRepository.findByRegistrationId(dto.getClientRegistrationId());
                if (registration == null) continue;

                OAuth2AuthorizedClient client = getClient(dto, registration);

                clientCache.put(entry.getKey(), client);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OAuth2 token from file", e);
        }
    }

    private static @NotNull OAuth2AuthorizedClient getClient(
            OAuth2AuthorizedClientDTO dto, ClientRegistration registration) {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                dto.getAccessTokenValue(),
                dto.getAccessTokenIssuedAt(),
                dto.getAccessTokenExpiresAt());

        OAuth2RefreshToken refreshToken = null;
        if (dto.getRefreshTokenValue() != null) {
            refreshToken = new OAuth2RefreshToken(dto.getRefreshTokenValue(), dto.getRefreshTokenIssuedAt());
        }

        return new OAuth2AuthorizedClient(registration, dto.getPrincipalName(), accessToken, refreshToken);
    }

    private String key(String clientRegistrationId, String principalName) {
        return clientRegistrationId + "::" + principalName;
    }
}
