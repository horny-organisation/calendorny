package ru.calendorny.notificationservice.entity;


import java.time.LocalDateTime;
import java.util.UUID;

public record EventReminderRequest(
    Long eventId,
    UUID userId,
    String title,
    String location,
    LocalDateTime start,
    LocalDateTime end
) {
}
