package ru.calendorny.dto;


import java.util.List;

public record ReminderDto(
    List<Integer> minutesBefore,
    Integer reminderMethodId
) {
}
