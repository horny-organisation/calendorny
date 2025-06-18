package ru.calendorny.eventservice.quartz.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.exception.ServiceException;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import ru.calendorny.eventservice.kafka.producer.KafkaEventProducer;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderJob implements Job {

    private final KafkaEventProducer kafkaEventProducer;

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap data = context.getMergedJobDataMap();
            EventReminderRequest request = EventReminderRequest.builder()
                .eventId(data.getLong("eventId"))
                .userId(UUID.fromString(data.getString("userId")))
                .title(data.getString("title"))
                .location(data.getString("location"))
                //.start(LocalDateTime.parse(data.getString("start")))
                //.end(LocalDateTime.parse(data.getString("end")))
                .build();
            kafkaEventProducer.send(request);
            log.info("Kafka sended in %s".formatted(LocalDateTime.now()));
        } catch (NullPointerException | IllegalArgumentException | DateTimeParseException e) {
            throw new ServiceException("Invalid job data format");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("SCHEDULE ERROR {}", e.getMessage());
            throw new ServiceException("Failed to execute reminder job");
        }
    }
}
