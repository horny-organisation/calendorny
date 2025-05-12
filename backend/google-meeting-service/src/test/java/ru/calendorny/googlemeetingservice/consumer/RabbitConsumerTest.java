package ru.calendorny.googlemeetingservice.consumer;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.calendorny.googlemeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.producer.RabbitProducerService;
import ru.calendorny.googlemeetingservice.service.SpaceCreatingService;

@ExtendWith(MockitoExtension.class)
public class RabbitConsumerTest {

    private static final Long EVENT_ID = 123L;
    private static final String EXPECTED_LINK = "https://meet.google.com/test";

    @Mock
    private SpaceCreatingService creatingService;

    @Mock
    private RabbitProducerService producerService;

    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    @Test
    void processQueueShouldCreateMeetingAndSendResponse() {
        LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
        MeetingCreateRequest request = new MeetingCreateRequest(EVENT_ID, startTime);

        when(creatingService.createMeetSpace()).thenReturn(EXPECTED_LINK);

        rabbitConsumer.processQueue(request);

        verify(creatingService).createMeetSpace();
        verify(producerService).sendMessage(new MeetingResponse(EVENT_ID, EXPECTED_LINK));
    }
}
