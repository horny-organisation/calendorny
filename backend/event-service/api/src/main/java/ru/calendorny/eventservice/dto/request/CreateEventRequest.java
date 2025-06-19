package ru.calendorny.eventservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.calendorny.eventservice.dto.ParticipantDto;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.MeetingType;

import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
    @NotBlank(message = "Event title should not be empty")
    @Size(max = 200, message = "Event title should be longer than {} characters")
    String title,

    @Size(max = 1000, message = "Event title should be longer than {} characters")
    String description,

    @Size(max = 255, message = "Event title should be longer than {} characters")
    String location,

    @NotNull(message = "Event start should not be empty")
    LocalDateTime start,

    @NotNull(message = "Event end should not be empty")
    LocalDateTime end,
    RruleDto rrule,
    List<Long> labels,
    boolean isMeeting,
    MeetingType meetingType,
    List<ParticipantDto> participants,
    ReminderDto reminder
) {
}
