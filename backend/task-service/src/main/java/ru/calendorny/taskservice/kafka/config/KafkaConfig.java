package ru.calendorny.taskservice.kafka.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.calendorny.taskservice.kafka.properties.KafkaConfigProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final KafkaConfigProperties kafkaConfig;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public NewTopic taskTopic() {
        return TopicBuilder.name(kafkaConfig.taskNotificationTopic())
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic taskDlqTopic() {
        return TopicBuilder.name(kafkaConfig.taskNotificationDlqTopic())
            .partitions(1)
            .replicas(1)
            .build();
    }
}
