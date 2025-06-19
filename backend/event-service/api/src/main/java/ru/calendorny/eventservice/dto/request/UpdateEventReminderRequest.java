package ru.calendorny.eventservice.dto.request;

import ru.calendorny.eventservice.dto.ReminderDto;

public record UpdateEventReminderRequest(
    ReminderDto reminder
) {
}
