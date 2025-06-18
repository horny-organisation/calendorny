package ru.calendorny.taskservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;
import ru.calendorny.taskservice.kafka.properties.KafkaConfigProperties;

@Service
@RequiredArgsConstructor
public class KafkaTaskProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final KafkaConfigProperties kafkaConfigProperties;

    public void send(TodayTaskEvent event) {
        kafkaTemplate.send(kafkaConfigProperties.taskNotificationTopic(), event);
    }
}
