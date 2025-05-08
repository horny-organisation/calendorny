package ru.calendorny.googlemeetingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.calendorny.googlemeetingservice.dto.request.MeetingCreateRequest;
import ru.calendorny.googlemeetingservice.dto.response.MeetingResponse;
import ru.calendorny.googlemeetingservice.producer.RabbitProducerService;
import ru.calendorny.googlemeetingservice.service.SpaceCreatingService;

@Component
@EnableRabbit
@Slf4j
@RequiredArgsConstructor
public class RabbitConsumer {

    private final SpaceCreatingService creatingService;
    private final RabbitProducerService producerService;

    @RabbitListener(queues = "${app.rabbit.googleMeetQueue}")
    public void processQueue(MeetingCreateRequest request) {
        String link = creatingService.createMeetSpace();

        MeetingResponse response = new MeetingResponse(request.eventId(), link);

        producerService.sendMessage(response);
    }
}
