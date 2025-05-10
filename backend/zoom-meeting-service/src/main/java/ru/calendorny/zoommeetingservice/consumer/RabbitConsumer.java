package ru.calendorny.zoommeetingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.calendorny.zoommeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.zoommeetingservice.dto.response.MeetingResponse;
import ru.calendorny.zoommeetingservice.producer.RabbitProducerService;
import ru.calendorny.zoommeetingservice.service.MeetingCreatingService;

@Component
@EnableRabbit
@Slf4j
@RequiredArgsConstructor
public class RabbitConsumer {

    private final MeetingCreatingService creatingService;
    private final RabbitProducerService producerService;

    @RabbitListener(queues = "${app.rabbit.zoomMeetQueue}")
    public void processQueue(MeetingCreateRequest request) {
        String link = creatingService.createMeeting(request.startDateTime());

        MeetingResponse response = new MeetingResponse(request.eventId(), link);

        producerService.sendMessage(response);
    }
}
