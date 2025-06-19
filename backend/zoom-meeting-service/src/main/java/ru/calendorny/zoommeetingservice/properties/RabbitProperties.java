package ru.calendorny.zoommeetingservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbit")
public record RabbitProperties(
        String zoomMeetQueue,
        String meetingLinksQueue,
        String meetingCreateExchange,
        String meetingLinksExchange,
        String zoomMeetRoutingKey,
        String meetingLinkRoutingKey,
        String deadLetterExchange,
        String zoomMeetDlq,
        String zoomMeetDlqRoutingKey) {}
