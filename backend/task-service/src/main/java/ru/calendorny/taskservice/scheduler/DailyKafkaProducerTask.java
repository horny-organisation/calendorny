package ru.calendorny.taskservice.scheduler;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.kafka.KafkaTaskEventProducer;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.service.impl.RecurTaskProcessor;
import ru.calendorny.taskservice.service.impl.SingleTaskProcessor;
import ru.calendorny.taskservice.util.RruleCalculator;
import ru.calendorny.taskservice.util.RruleConverter;

@Component
public class DailyKafkaProducerTask {

    private final SingleTaskProcessor singleTaskProcessor;

    private final RecurTaskProcessor recurTaskProcessor;

    private final KafkaTaskEventProducer kafkaTaskEventProducer;

    private final TaskMapper mapper;

    public DailyKafkaProducerTask(SingleTaskProcessor singleTaskProcessor, RecurTaskProcessor recurTaskProcessor, KafkaTaskEventProducer kafkaTaskEventProducer, TaskMapper mapper) {
        this.singleTaskProcessor = singleTaskProcessor;
        this.recurTaskProcessor = recurTaskProcessor;
        this.kafkaTaskEventProducer = kafkaTaskEventProducer;
        this.mapper = mapper;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void runAtMidnightUTC() {

        List<TaskResponse> singleTasks = singleTaskProcessor.getPendingTasksByDate(LocalDate.now(ZoneOffset.UTC));
        singleTasks.stream().map(mapper::fromResponseToEvent).forEach(kafkaTaskEventProducer::send);

        List<TaskResponse> recurTasks = recurTaskProcessor.getPendingTasksByDate(LocalDate.now(ZoneOffset.UTC));
        recurTasks.stream().map(mapper::fromResponseToEvent).forEach(kafkaTaskEventProducer::send);
        recurTasks.forEach(t -> {
            singleTaskProcessor.createTask(t.getUserId(), t.getTitle(), t.getDescription(), t.getDueDate(), null);
            String rruleString = RruleConverter.toRruleString(t.getRecurrenceRule());
            recurTaskProcessor.updateTask(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                RruleCalculator.findNextDate(rruleString, t.getDueDate()),
                t.getStatus(),
                t.getRecurrenceRule());
        });
    }
}
