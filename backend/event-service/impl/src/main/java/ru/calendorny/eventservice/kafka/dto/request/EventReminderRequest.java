package ru.calendorny.eventservice.kafka.dto.request;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventReminderRequest(
    Long eventId,
    UUID userId,
    String title,
    String location
) {
}
