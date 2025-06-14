package ru.calendorny.eventservice.dto;


import lombok.Builder;
import java.util.List;

@Builder
public record ReminderDto(
    List<Integer> minutesBefore
) {
}
