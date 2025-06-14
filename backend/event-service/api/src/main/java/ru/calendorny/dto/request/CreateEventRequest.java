package ru.calendorny.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.calendorny.dto.ReminderDto;
import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.MeetingType;
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
    LocalDateTime startTime,

    @NotNull(message = "Event end should not be empty")
    LocalDateTime endTime,
    RruleDto rrule,
    List<Long> labels,
    boolean isMeeting,
    MeetingType meetingType,
    List<String> participantEmails,
    ReminderDto reminder
) {
}
