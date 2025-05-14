package ru.calendorny.taskservice.kafka;

import static ru.calendorny.taskservice.kafka.KafkaConstants.*;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;

@Service
@RequiredArgsConstructor
public class KafkaTaskEventProducer {

    private final KafkaTemplate<String, TodayTaskEvent> kafkaTemplate;

    public void send(TodayTaskEvent event) {
        kafkaTemplate.send(TASK_TOPIC_NAME, event);
    }
}
