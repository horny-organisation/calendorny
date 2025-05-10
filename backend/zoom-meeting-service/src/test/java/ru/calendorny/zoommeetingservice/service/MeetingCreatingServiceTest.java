package ru.calendorny.zoommeetingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingRequest;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingResponse;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;

@ExtendWith(MockitoExtension.class)
public class MeetingCreatingServiceTest {

    private static final String MEETING_LINK =
            "https://us04web.zoom.us/j/75902196185?pwd=0OLMat2ghNQJkSaJUSdfBdr4LhwLTv.1";
    private static final String MEETING_CREATE_URL = "https://api.zoom.us/v2/users/me/meetings";
    private static final String TIMEZONE = "UTC";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final LocalDateTime MEETING_START_TIME = LocalDateTime.now(ZoneId.of("UTC"));

    @Mock
    private ObjectFactory<WebClient> zoomWebClientFactory;

    @Mock
    private WebClient zoomWebClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private ZoomProperties zoomProperties;

    @Mock
    private ZoomMeetingResponse zoomMeetingResponse;

    @InjectMocks
    private MeetingCreatingService meetingCreatingService;

    @BeforeEach
    void setUp() {
        when(zoomProperties.meetingCreateUrl()).thenReturn(MEETING_CREATE_URL);
        when(zoomProperties.timezone()).thenReturn(TIMEZONE);
        when(zoomProperties.dateTimePattern()).thenReturn(DATETIME_PATTERN);
        when(zoomWebClientFactory.getObject()).thenReturn(zoomWebClient);
    }

    @Test
    void shouldReturnMeetingLinkWhenApiCallIsSuccessful() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);

        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ZoomMeetingResponse.class)).thenReturn(Mono.just(zoomMeetingResponse));
        when(zoomMeetingResponse.joinUrl()).thenReturn(MEETING_LINK);

        String result = meetingCreatingService.createMeeting(MEETING_START_TIME);

        assertEquals(MEETING_LINK, result);
    }

    @Test
    void shouldReturnErrorWhenApiCallFails() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenThrow(new RuntimeException("Zoom API call failed"));

        String result = meetingCreatingService.createMeeting(MEETING_START_TIME);

        assertEquals("Error: Unexpected response", result);
    }

    @Test
    void shouldReturnErrorWhenJoinUrlIsNull() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ZoomMeetingResponse.class)).thenReturn(Mono.just(zoomMeetingResponse));
        when(zoomMeetingResponse.joinUrl()).thenReturn(null);

        String result = meetingCreatingService.createMeeting(MEETING_START_TIME);

        assertEquals("Error: Unexpected response", result);
    }
}
