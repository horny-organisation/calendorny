package ru.calendorny.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record BotKafkaProperties(String groupId, String taskNotificationTopic) {}
