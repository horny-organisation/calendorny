package ru.calendorny.googlemeetingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.apps.meet.v2.CreateSpaceRequest;
import com.google.apps.meet.v2.Space;
import com.google.apps.meet.v2.SpacesServiceClient;
import com.google.auth.Credentials;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import ru.calendorny.googlemeetingservice.factory.MeetClientFactory;

@ExtendWith(MockitoExtension.class)
class SpaceCreatingServiceTest {

    private static final String TOKEN_VALUE = "test-token";
    private static final String MEETING_URI = "https://meet.google.com/success";

    @Mock
    private MeetClientFactory meetClientFactory;

    @Mock
    private SpacesServiceClient spacesServiceClient;

    @Mock
    private OAuth2AuthorizedClient client;

    @InjectMocks
    private SpaceCreatingService service;

    @BeforeEach
    void init() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                TOKEN_VALUE,
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(600));
        lenient().when(client.getAccessToken()).thenReturn(accessToken);
    }

    @Test
    void shouldReturnUnauthorizedErrorWhenClientIsNull() {
        SpaceCreatingService serviceWithoutClient = new SpaceCreatingService(meetClientFactory, null);

        String result = serviceWithoutClient.createMeetSpace();

        assertEquals("Error: Unauthorized", result);
    }

    @Test
    void shouldReturnMeetingUriWhenSpaceIsCreatedSuccessfully() throws IOException {
        when(meetClientFactory.create(any(Credentials.class))).thenReturn(spacesServiceClient);

        Space created = Space.newBuilder().setMeetingUri(MEETING_URI).build();
        when(spacesServiceClient.createSpace(any(CreateSpaceRequest.class))).thenReturn(created);

        String result = service.createMeetSpace();

        assertEquals(MEETING_URI, result);
        verify(spacesServiceClient).createSpace(any(CreateSpaceRequest.class));
    }

    @Test
    void shouldReturnErrorWhenSpaceCreationFails() throws IOException {
        when(meetClientFactory.create(any(Credentials.class))).thenThrow(new RuntimeException("Boom"));

        String result = service.createMeetSpace();

        assertEquals("Error creating space", result);
    }
}
