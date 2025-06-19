package ru.calendorny.googlemeetingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.calendorny.googlemeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.googlemeetingservice.service.SpaceCreatingService;

@Component
@EnableRabbit
@Slf4j
@RequiredArgsConstructor
public class RabbitConsumer {

    private final SpaceCreatingService creatingService;

    @RabbitListener(queues = "${app.rabbit.googleMeetQueue}")
    public void processQueue(MeetingCreateRequest request) {
        creatingService.createMeetSpace(request.eventId());
    }
}
