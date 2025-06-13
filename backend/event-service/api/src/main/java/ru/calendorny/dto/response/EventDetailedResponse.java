package ru.calendorny.dto.response;

import lombok.Builder;
import ru.calendorny.dto.LabelDto;
import ru.calendorny.dto.ReminderDto;
import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.MeetingType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record EventDetailedResponse(
    UUID id,
    UUID userId,
    String title,
    String description,
    String location,
    LocalDateTime startTime,
    LocalDateTime endTime,
    RruleDto rrule,
    LabelDto label,
    boolean isMeeting,
    MeetingType meetingType,
    String videoMeetingUrl,
    List<String> participantEmails,
    ReminderDto reminder
) {
}
