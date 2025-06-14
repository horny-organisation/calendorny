package ru.calendorny.eventservice.kafka.dto.request;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventNotificationRequest(
    Long eventId,
    UUID userId,
    String title,
    String location,
    LocalDateTime start,
    LocalDateTime end
) {
}
