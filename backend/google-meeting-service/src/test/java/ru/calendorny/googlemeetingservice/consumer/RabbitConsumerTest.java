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

    @Mock
    private SpaceCreatingService creatingService;


    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    @Test
    void processQueueShouldCreateMeetingAndSendResponse() {
        LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
        MeetingCreateRequest request = new MeetingCreateRequest(EVENT_ID, startTime);

        rabbitConsumer.processQueue(request);

        verify(creatingService).createMeetSpace(EVENT_ID);
    }
}
