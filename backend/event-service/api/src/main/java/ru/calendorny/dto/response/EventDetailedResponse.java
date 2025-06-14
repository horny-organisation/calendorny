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
    String title,
    String description,
    String location,
    LocalDateTime startTime,
    LocalDateTime endTime,
    RruleDto rrule,
    List<LabelDto> labels,
    boolean isMeeting,
    MeetingType meetingType,
    String videoMeetingUrl,
    UUID organizerId,
    List<String> participantEmails,
    List<ReminderDto> reminder
) {
}
