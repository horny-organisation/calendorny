package ru.calendorny.notificationservice.entity;


import java.util.UUID;

public record EventReminderRequest(
    Long eventId,
    UUID userId,
    String title,
    String location
) {
}
