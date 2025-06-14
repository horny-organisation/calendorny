package ru.calendorny.eventservice.kafka.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaConfigProperties(
    String eventNotificationTopic,
    String eventNotificationDlqTopic
){}
