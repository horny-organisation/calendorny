package ru.calendorny.eventservice.quartz.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.kafka.producer.KafkaEventProducer;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class QuartzJobSupport implements Job {

    private KafkaEventProducer kafkaEventProducer;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getMergedJobDataMap();

        EventNotificationRequest request = EventNotificationRequest.builder()
            .eventId(data.getLong("eventId"))
            .userId(UUID.fromString(data.getString("userId")))
            .title(data.getString("title"))
            .location(data.getString("location"))
            .start(LocalDateTime.parse(data.getString("start")))
            .end(LocalDateTime.parse(data.getString("end")))
            .build();

        kafkaEventProducer.send(request);
    }
}

