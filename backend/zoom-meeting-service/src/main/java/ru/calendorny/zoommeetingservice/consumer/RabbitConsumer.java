package ru.calendorny.zoommeetingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.calendorny.zoommeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.zoommeetingservice.service.MeetingCreatingService;

@Component
@EnableRabbit
@Slf4j
@RequiredArgsConstructor
public class RabbitConsumer {

    private final MeetingCreatingService creatingService;

    @RabbitListener(queues = "${app.rabbit.zoomMeetQueue}")
    public void processQueue(MeetingCreateRequest request) {
        creatingService.createMeeting(request.eventId(), request.startDateTime());
    }
}
