package ru.calendorny.eventservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.kafka.dto.request.EventReminderRequest;
import ru.calendorny.eventservice.kafka.properties.KafkaConfigProperties;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigProperties kafkaConfigProperties;

    public void send(EventReminderRequest request) {
        kafkaTemplate.send(kafkaConfigProperties.eventNotificationTopic(), request);
    }

}
