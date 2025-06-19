package ru.calendorny.googlemeetingservice.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.properties.RabbitProperties;

@Service
@RequiredArgsConstructor
public class RabbitProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties properties;

    public void sendMessage(MeetingResponse message) {
        rabbitTemplate.convertAndSend(properties.meetingLinksExchange(), properties.meetingLinkRoutingKey(), message);
    }
}
