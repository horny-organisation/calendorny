package ru.calendorny.zoommeetingservice.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.zoommeetingservice.TestcontainersConfiguration;
import ru.calendorny.zoommeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.zoommeetingservice.dto.response.MeetingResponse;
import ru.calendorny.zoommeetingservice.producer.RabbitProducerService;
import ru.calendorny.zoommeetingservice.properties.ZoomProperties;
import ru.calendorny.zoommeetingservice.service.MeetingCreatingService;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class RabbitConsumerIntegrationTest {

    private static final Long TEST_EVENT_ID = 123L;
    private static final String TEST_PRINCIPAL = "testPrincipal";
    private static final String TEST_CLIENT_REGISTRATION_ID = "testClientRegistrationId";
    private static final String EXPECTED_MEET_LINK =
            "https://us04web.zoom.us/j/75902196185?pwd=0OLMat2ghNQJkSaJUSdfBdr4LhwLTv.1";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue zoomMeetQueue;

    @MockitoBean
    private ZoomProperties properties;

    @MockitoBean
    private MeetingCreatingService creatingService;

    @MockitoBean
    private RabbitProducerService producerService;

    private MeetingCreateRequest meetingCreateRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
        meetingCreateRequest = new MeetingCreateRequest(TEST_EVENT_ID, startTime);

        when(properties.principalName()).thenReturn(TEST_PRINCIPAL);
        when(properties.clientRegistrationId()).thenReturn(TEST_CLIENT_REGISTRATION_ID);
        when(creatingService.createMeeting(startTime)).thenReturn(EXPECTED_MEET_LINK);
    }

    @Test
    void testProcessQueue() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend(zoomMeetQueue.getName(), meetingCreateRequest);

        ArgumentCaptor<MeetingResponse> responseCaptor = ArgumentCaptor.forClass(MeetingResponse.class);

        verify(producerService, timeout(5000).times(1)).sendMessage(responseCaptor.capture());

        MeetingResponse capturedResponse = responseCaptor.getValue();
        System.out.println(capturedResponse);

        assertEquals(TEST_EVENT_ID, capturedResponse.eventId());
        assertEquals(EXPECTED_MEET_LINK, capturedResponse.link());
    }
}
