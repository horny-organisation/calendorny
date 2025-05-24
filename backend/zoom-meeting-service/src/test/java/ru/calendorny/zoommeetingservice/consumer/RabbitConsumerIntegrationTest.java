package ru.calendorny.zoommeetingservice.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.zoommeetingservice.TestcontainersConfiguration;
import ru.calendorny.zoommeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.zoommeetingservice.service.MeetingCreatingService;

@SpringBootTest
@TestPropertySource(
        properties = {
            "app.zoom.principal-name=testPrincipal",
            "app.zoom.client-registration-id=testClientRegistrationId",
            "app.zoom.file-name=test-oauth-tokens.json",
            "encryption.aes.key=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0",
            "app.rabbit.zoomMeetQueue=zoomMeetQueue"
        })
@Import(TestcontainersConfiguration.class)
public class RabbitConsumerIntegrationTest {

    private static final Long TEST_EVENT_ID = 123L;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue zoomMeetQueue;

    @MockitoBean
    private MeetingCreatingService creatingService;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
        meetingCreateRequest = new MeetingCreateRequest(TEST_EVENT_ID, startTime);
    }

    private MeetingCreateRequest meetingCreateRequest;

    @Test
    void testProcessQueue() {
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.convertAndSend(zoomMeetQueue.getName(), meetingCreateRequest);

        verify(creatingService, timeout(5000).times(1)).createMeeting(eq(TEST_EVENT_ID), any());
    }
}
