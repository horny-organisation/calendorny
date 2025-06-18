package ru.calendorny.taskservice.scheduler;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.kafka.producer.KafkaTaskProducer;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.service.impl.RecurTaskProcessor;
import ru.calendorny.taskservice.service.impl.SingleTaskProcessor;
import ru.calendorny.taskservice.util.rrule.RruleCalculator;
import ru.calendorny.taskservice.util.rrule.RruleConverter;

@Component
@RequiredArgsConstructor
public class DailyKafkaProducerTask {

    private final SingleTaskProcessor singleTaskProcessor;

    private final RecurTaskProcessor recurTaskProcessor;

    private final RruleConverter rruleConverter;

    private final RruleCalculator rruleCalculator;

    private final KafkaTaskProducer kafkaTaskEventProducer;

    private final TaskMapper mapper;

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void runAtMidnightUTC() {

        List<TaskResponse> singleTasks = singleTaskProcessor.getPendingTasksByDate(LocalDate.now(ZoneOffset.UTC));

        singleTasks.stream()
            .map(mapper::fromResponseToEvent)
            .forEach(kafkaTaskEventProducer::send);

        List<TaskResponse> recurTasks = recurTaskProcessor.getPendingTasksByDate(LocalDate.now(ZoneOffset.UTC));

        recurTasks.stream()
            .map(mapper::fromResponseToEvent)
            .forEach(kafkaTaskEventProducer::send);

        recurTasks.forEach(t -> {
            singleTaskProcessor.createTask(t.userId(), t.title(), t.description(), t.dueDate(), null);
            String rruleString = rruleConverter.toRruleString(t.recurrenceRule());
            recurTaskProcessor.updateTask(
                t.id(),
                t.title(),
                t.description(),
                rruleCalculator.findNextDate(rruleString, t.dueDate()),
                t.status(),
                t.recurrenceRule());
        });
    }
}
