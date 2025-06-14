package ru.calendorny.eventservice.dto.response;

import lombok.Builder;
import ru.calendorny.eventservice.dto.LabelDto;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.MeetingType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record EventDetailedResponse(
    Long id,
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
