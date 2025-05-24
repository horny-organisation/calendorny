package ru.calendorny.zoommeetingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingRequest;
import ru.calendorny.zoommeetingservice.dto.meeting.ZoomMeetingResponse;
import ru.calendorny.zoommeetingservice.dto.response.MeetingResponse;
import ru.calendorny.zoommeetingservice.producer.RabbitProducerService;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;

@ExtendWith(MockitoExtension.class)
public class MeetingCreatingServiceTest {

    private static final String MEETING_LINK =
            "https://us04web.zoom.us/j/75902196185?pwd=0OLMat2ghNQJkSaJUSdfBdr4LhwLTv.1";
    private static final String MEETING_CREATE_URL = "https://api.zoom.us/v2/users/me/meetings";
    private static final String TIMEZONE = "UTC";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";

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

    @Mock
    private RabbitProducerService producerService;

    @InjectMocks
    private MeetingCreatingService meetingCreatingService;

    private LocalDateTime meetingStartTime;

    @BeforeEach
    void setUp() {
        when(zoomProperties.meetingCreateUrl()).thenReturn(MEETING_CREATE_URL);
        when(zoomProperties.timezone()).thenReturn(TIMEZONE);
        when(zoomProperties.dateTimePattern()).thenReturn(DATETIME_PATTERN);
        when(zoomWebClientFactory.getObject()).thenReturn(zoomWebClient);

        meetingStartTime = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Test
    void shouldSendMeetingResponse_whenApiCallIsSuccessful() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ZoomMeetingResponse.class)).thenReturn(Mono.just(zoomMeetingResponse));
        when(zoomMeetingResponse.joinUrl()).thenReturn(MEETING_LINK);

        Long eventId = 123L;
        meetingCreatingService.createMeeting(eventId, meetingStartTime);

        ArgumentCaptor<MeetingResponse> captor = ArgumentCaptor.forClass(MeetingResponse.class);
        verify(producerService, times(1)).sendMessage(captor.capture());

        MeetingResponse sentResponse = captor.getValue();
        assertEquals(eventId, sentResponse.eventId());
        assertEquals(MEETING_LINK, sentResponse.link());
    }

    @Test
    void shouldSendErrorMessage_whenApiCallFails() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenThrow(new RuntimeException("Zoom API failure"));

        Long eventId = 123L;
        meetingCreatingService.createMeeting(eventId, meetingStartTime);

        ArgumentCaptor<MeetingResponse> captor = ArgumentCaptor.forClass(MeetingResponse.class);
        verify(producerService, times(1)).sendMessage(captor.capture());

        MeetingResponse sentResponse = captor.getValue();
        assertEquals(eventId, sentResponse.eventId());
        assertEquals("Error: Unexpected response", sentResponse.link());
    }

    @Test
    void shouldSendErrorMessage_whenJoinUrlIsNull() {
        when(zoomWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(MEETING_CREATE_URL)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).bodyValue(any(ZoomMeetingRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ZoomMeetingResponse.class)).thenReturn(Mono.just(zoomMeetingResponse));
        when(zoomMeetingResponse.joinUrl()).thenReturn(null);

        Long eventId = 123L;
        meetingCreatingService.createMeeting(eventId, meetingStartTime);

        ArgumentCaptor<MeetingResponse> captor = ArgumentCaptor.forClass(MeetingResponse.class);
        verify(producerService, times(1)).sendMessage(captor.capture());

        MeetingResponse sentResponse = captor.getValue();
        assertEquals(eventId, sentResponse.eventId());
        assertEquals("Error: Unexpected response", sentResponse.link());
    }
}
