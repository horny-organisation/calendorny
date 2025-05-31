package ru.calendorny.zoommeetingservice.consumer;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.calendorny.zoommeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.zoommeetingservice.producer.RabbitProducerService;
import ru.calendorny.zoommeetingservice.service.MeetingCreatingService;

@ExtendWith(MockitoExtension.class)
public class RabbitConsumerTest {

    private static final Long EVENT_ID = 123L;
    private static final String EXPECTED_LINK =
            "https://us04web.zoom.us/j/75902196185?pwd=0OLMat2ghNQJkSaJUSdfBdr4LhwLTv.1";

    @Mock
    private MeetingCreatingService creatingService;

    @Mock
    private RabbitProducerService producerService;

    @InjectMocks
    private RabbitConsumer rabbitConsumer;

    @Test
    void processQueueShouldCreateMeetingAndSendResponse() {
        LocalDateTime startTime = LocalDateTime.now(ZoneId.systemDefault());
        MeetingCreateRequest request = new MeetingCreateRequest(EVENT_ID, startTime);

        rabbitConsumer.processQueue(request);

        verify(creatingService).createMeeting(123L, startTime);
    }
}
