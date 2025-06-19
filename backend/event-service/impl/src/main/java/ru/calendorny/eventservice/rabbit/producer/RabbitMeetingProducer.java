package ru.calendorny.eventservice.rabbit.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.rabbit.dto.request.MeetingCreateRequest;
import ru.calendorny.eventservice.rabbit.properties.RabbitConfigProperties;

@Component
@RequiredArgsConstructor
public class RabbitMeetingProducer {

    private final RabbitTemplate rabbitTemplate;

    private final RabbitConfigProperties properties;

    public void sendZoomMeetingCreationRequest(MeetingCreateRequest request) {
        rabbitTemplate.convertAndSend(
            properties.meetingCreateExchange(),
            properties.zoomMeetRoutingKey(),
            request
        );
    }

    public void sendGoogleMeetingCreationRequest(MeetingCreateRequest request) {
        rabbitTemplate.convertAndSend(
            properties.meetingCreateExchange(),
            properties.googleMeetRoutingKey(),
            request
        );
    }
}
