package ru.calendorny.googlemeetingservice.service;

import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import ru.calendorny.googlemeetingservice.factory.MeetClientFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceCreatingService {

    private final MeetClientFactory meetClientFactory;
    private final OAuth2AuthorizedClient client;

    public String createMeetSpace() {
        if (client == null) {
            log.error("Client is null");
            return "Error: Unauthorized";
        }

        Credentials credentials = toGoogleCredentials(client);

        return doCreateSpace(credentials).map(Space::getMeetingUri).orElse("Error creating space");
    }

    private Credentials toGoogleCredentials(OAuth2AuthorizedClient client) {
        String token = client.getAccessToken().getTokenValue();
        Instant expiresAt = client.getAccessToken().getExpiresAt();
        Date expirationDate = (expiresAt != null) ? Date.from(expiresAt) : null;

        return GoogleCredentials.create(AccessToken.newBuilder()
                .setTokenValue(token)
                .setExpirationTime(expirationDate)
                .build());
    }

    private Optional<Space> doCreateSpace(Credentials credentials) {
        try (SpacesServiceClient svc = meetClientFactory.create(credentials)) {
            CreateSpaceRequest req = CreateSpaceRequest.newBuilder()
                    .setSpace(Space.newBuilder().build())
                    .build();

            return Optional.of(svc.createSpace(req));
        } catch (Exception e) {
            log.error("Meet API error", e);
            return Optional.empty();
        }
    }
}
