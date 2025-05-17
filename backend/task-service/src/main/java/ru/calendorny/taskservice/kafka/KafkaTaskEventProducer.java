package ru.calendorny.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;

@Service
@RequiredArgsConstructor
public class KafkaTaskEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final KafkaConfigProperties kafkaConfigProperties;

    public void send(TodayTaskEvent event) {
        kafkaTemplate.send(kafkaConfigProperties.taskNotificationTopic(), event);
    }
}
