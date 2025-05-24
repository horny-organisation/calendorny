package ru.calendorny.googlemeetingservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.factory.MeetClientFactory;
import ru.calendorny.googlemeetingservice.producer.RabbitProducerService;

@ExtendWith(MockitoExtension.class)
class SpaceCreatingServiceTest {

    private static final String TOKEN_VALUE = "test-token";
    private static final String MEETING_URI = "https://meet.google.com/success";
    private static final Long EVENT_ID = 42L;

    @Mock
    private MeetClientFactory meetClientFactory;

    @Mock
    private SpacesServiceClient spacesServiceClient;

    @Mock
    private OAuth2AuthorizedClient client;

    @Mock
    private RabbitProducerService producerService;

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
    void shouldSendErrorResponseWhenClientIsNull() {
        SpaceCreatingService serviceWithoutClient = new SpaceCreatingService(meetClientFactory, null, producerService);

        serviceWithoutClient.createMeetSpace(EVENT_ID);

        verify(producerService).sendMessage(new MeetingResponse(EVENT_ID, "Error creating space"));
    }

    @Test
    void shouldSendMeetingUriWhenSpaceIsCreatedSuccessfully() throws IOException {
        when(meetClientFactory.create(any(Credentials.class))).thenReturn(spacesServiceClient);

        Space created = Space.newBuilder().setMeetingUri(MEETING_URI).build();
        when(spacesServiceClient.createSpace(any(CreateSpaceRequest.class))).thenReturn(created);

        service.createMeetSpace(EVENT_ID);

        verify(producerService).sendMessage(new MeetingResponse(EVENT_ID, MEETING_URI));
        verify(spacesServiceClient).createSpace(any(CreateSpaceRequest.class));
    }

    @Test
    void shouldSendErrorResponseWhenSpaceCreationFails() throws IOException {
        lenient().when(meetClientFactory.create(any(Credentials.class))).thenThrow(new RuntimeException("Boom"));

        service.createMeetSpace(EVENT_ID);

        verify(producerService).sendMessage(new MeetingResponse(EVENT_ID, "Error creating space"));
    }
}
