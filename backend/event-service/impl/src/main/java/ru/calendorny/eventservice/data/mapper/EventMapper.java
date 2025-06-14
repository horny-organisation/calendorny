package ru.calendorny.eventservice.data.mapper;

import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import ru.calendorny.dto.LabelDto;
import ru.calendorny.dto.ReminderDto;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventDetailedResponse toDetailedResponse (EventEntity eventEntity) {
        return EventDetailedResponse.builder()
            .id(eventEntity.getId())
            .title(eventEntity.getTitle())
            .description(eventEntity.getDescription())
            .location(eventEntity.getLocation())
            .startTime(eventEntity.getStart())
            .endTime(eventEntity.getEnd())
            .rrule(eventEntity.getRrule())
            .labels(eventEntity.getLabels().stream().map(this::eventLabelToDto).collect(Collectors.toList()))
            .isMeeting(eventEntity.isMeeting())
            .meetingType(eventEntity.getMeetingType())
            .videoMeetingUrl(eventEntity.getVideoMeetingUrl())
            .participantEmails(eventEntity.getParticipants().stream().map(ParticipantEntity::getEmail).collect(Collectors.toList()))
            .reminder(reminderToDto(eventEntity.getReminders()))
            .organizerId(eventEntity.getOrganizerId())
            .build();
    }

    public EventShortResponse toShortResponse (EventEntity eventEntity) {
        return EventShortResponse.builder()
            .id(eventEntity.getId())
            .title(eventEntity.getTitle())
            .location(eventEntity.getLocation())
            .startTime(eventEntity.getStart())
            .endTime(eventEntity.getEnd())
            .labels(eventEntity.getLabels().stream().map(this::eventLabelToDto).collect(Collectors.toList()))
            .build();
    }

    LabelDto eventLabelToDto(EventLabelEntity entity) {
        return LabelDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .color(entity.getColor())
            .build();
    }

    List<ReminderDto> reminderToDto(List<ReminderEntity> reminderEntities) {
        List<ReminderDto> reminderDtos = new ArrayList<>();
        for (ReminderEntity reminder : reminderEntities) {
            if (reminderDtos.get(reminder.getReminderMethod().getId()) == null) {
                reminderDtos.add(
                    ReminderDto.builder()
                        .reminderMethodId(reminder.getReminderMethod().getId())
                        .build()
                );
            }
            ReminderDto reminderDto = reminderDtos.get(reminder.getReminderMethod().getId());
            reminderDto.minutesBefore().add(reminder.getMinutesBefore());
        }
        return reminderDtos;
    }
}
