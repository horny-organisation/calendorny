package ru.calendorny.googlemeetingservice.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.googlemeetingservice.TestcontainersConfiguration;
import ru.calendorny.googlemeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.producer.RabbitProducerService;
import ru.calendorny.googlemeetingservice.properties.GoogleOauthProperties;
import ru.calendorny.googlemeetingservice.service.SpaceCreatingService;

@SpringBootTest
@TestPropertySource(
    properties = {
        "app.google.oauth.principalName=testPrincipal",
        "app.google.oauth.clientRegistrationId=testClientRegistrationId",
        "app.google.oauth.fileName=test-oauth-tokens.json",
        "encryption.aes.key=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0",
        "app.rabbit.googleMeetQueue=googleMeetQueue"
    })
@Import(TestcontainersConfiguration.class)
public class RabbitConsumerIntegrationTest {

    private static final Long TEST_EVENT_ID = 123L;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue googleMeetQueue;

    @MockitoBean
    private SpaceCreatingService creatingService;

    private MeetingCreateRequest meetingCreateRequest;

    @BeforeEach
    void setUp() {
        meetingCreateRequest = new MeetingCreateRequest(TEST_EVENT_ID, null);
    }

    @Test
    void testProcessQueue() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend(googleMeetQueue.getName(), meetingCreateRequest);

        verify(creatingService, timeout(5000).times(1)).createMeetSpace(eq(TEST_EVENT_ID));
    }
}
