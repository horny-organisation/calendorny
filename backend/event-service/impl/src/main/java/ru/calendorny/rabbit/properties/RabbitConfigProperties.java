package ru.calendorny.rabbit.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbit")
public record RabbitConfigProperties(
    String googleMeetQueue,
    String meetingLinksQueue,
    String meetingCreateExchange,
    String meetingLinksExchange,
    String googleMeetRoutingKey,
    String meetingLinkRoutingKey,
    String deadLetterExchange,
    String googleMeetDlq,
    String googleMeetDlqRoutingKey,
    String zoomMeetQueue,
    String zoomMeetRoutingKey,
    String zoomMeetDlq,
    String zoomMeetDlqRoutingKey
) {
}

