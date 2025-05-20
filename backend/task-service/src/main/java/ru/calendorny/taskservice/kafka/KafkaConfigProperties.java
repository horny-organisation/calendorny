package ru.calendorny.taskservice.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "task.kafka")
public record KafkaConfigProperties(String taskNotificationTopic){}
