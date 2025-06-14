package ru.calendorny.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.calendorny.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.kafka.properties.KafkaConfigProperties;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaConfigProperties kafkaConfigProperties;

    public void send(EventNotificationRequest request) {
        kafkaTemplate.send(kafkaConfigProperties.eventNotificationTopic(), request);
    }

}
