package ru.calendorny.zoommeetingservice.producer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.calendorny.zoommeetingservice.dto.response.MeetingResponse;
import ru.calendorny.zoommeetingservice.properties.RabbitProperties;

@ExtendWith(MockitoExtension.class)
public class RabbitProducerServiceTest {

    private static final String TEST_EXCHANGE = "test-exchange";
    private static final String TEST_ROUTING_KEY = "test-routing-key";

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitProperties rabbitProperties;

    @InjectMocks
    private RabbitProducerService service;

    @Test
    void sendMessageShouldSendToCorrectExchangeAndRoutingKey() {
        MeetingResponse response = new MeetingResponse(0L, "");

        when(rabbitProperties.meetingLinksExchange()).thenReturn(TEST_EXCHANGE);
        when(rabbitProperties.meetingLinkRoutingKey()).thenReturn(TEST_ROUTING_KEY);

        service.sendMessage(response);

        verify(rabbitTemplate).convertAndSend(TEST_EXCHANGE, TEST_ROUTING_KEY, response);
    }
}
