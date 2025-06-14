package ru.calendorny.rabbit.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.calendorny.rabbit.dto.response.MeetingResponse;
import ru.calendorny.service.MeetingService;

@Component
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMeetingConsumer {

    private final MeetingService meetingService;

    @RabbitListener(queues = "${app.rabbit.meetingLinksQueue}")
    public void processMeetingResponse(MeetingResponse response) {
        meetingService.processMeetingResponse(response);
    }
}
