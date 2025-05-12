package ru.calendorny.zoommeetingservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.zoom")
public record ZoomProperties(
        String principalName,
        String clientRegistrationId,
        String baseUrl,
        String meetingCreateUrl,
        String timezone,
        String dateTimePattern) {}
