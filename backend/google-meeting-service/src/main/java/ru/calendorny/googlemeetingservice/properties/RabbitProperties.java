package ru.calendorny.googlemeetingservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbit")
public record RabbitProperties(
        String googleMeetQueue,
        String meetingLinksQueue,
        String meetingCreateExchange,
        String meetingLinksExchange,
        String googleMeetRoutingKey,
        String meetingLinkRoutingKey) {}
