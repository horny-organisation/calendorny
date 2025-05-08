package ru.calendorny.googlemeetingservice.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.googlemeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.producer.RabbitProducerService;
import ru.calendorny.googlemeetingservice.properties.GoogleOauthProperties;
import ru.calendorny.googlemeetingservice.service.SpaceCreatingService;

@SpringBootTest
class RabbitConsumerIntegrationTest {

    private static final Long TEST_EVENT_ID = 123L;
    private static final String TEST_PRINCIPAL = "testPrincipal";
    private static final String TEST_CLIENT_REGISTRATION_ID = "testClientRegistrationId";
    private static final String EXPECTED_MEET_LINK = "https://meet.google.com/abc-xyz";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue googleMeetQueue;

    @MockitoBean
    private GoogleOauthProperties properties;

    @MockitoBean
    private SpaceCreatingService creatingService;

    @MockitoBean
    private RabbitProducerService producerService;

    private MeetingCreateRequest meetingCreateRequest;

    @BeforeEach
    void setUp() {
        meetingCreateRequest = new MeetingCreateRequest(TEST_EVENT_ID, null);

        when(properties.principalName()).thenReturn(TEST_PRINCIPAL);
        when(properties.clientRegistrationId()).thenReturn(TEST_CLIENT_REGISTRATION_ID);
        when(creatingService.createMeetSpace()).thenReturn(EXPECTED_MEET_LINK);
    }

    @Test
    void testProcessQueue() {
        rabbitTemplate.convertAndSend(googleMeetQueue.getName(), meetingCreateRequest);

        ArgumentCaptor<MeetingResponse> responseCaptor = ArgumentCaptor.forClass(MeetingResponse.class);

        verify(producerService, timeout(5000).times(1)).sendMessage(responseCaptor.capture());

        MeetingResponse capturedResponse = responseCaptor.getValue();

        assertEquals(TEST_EVENT_ID, capturedResponse.eventId());
        assertEquals(EXPECTED_MEET_LINK, capturedResponse.link());
    }
}
