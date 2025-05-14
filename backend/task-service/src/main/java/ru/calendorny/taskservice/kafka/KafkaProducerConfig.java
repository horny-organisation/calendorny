package ru.calendorny.taskservice.kafka;

import static ru.calendorny.taskservice.kafka.KafkaConstants.*;

import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, TodayTaskEvent> kafkaTemplate(
            ProducerFactory<String, TodayTaskEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, TodayTaskEvent> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> config = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public NewTopic taskTopic() {
        return TopicBuilder.name(TASK_TOPIC_NAME).partitions(1).replicas(1).build();
    }
}
