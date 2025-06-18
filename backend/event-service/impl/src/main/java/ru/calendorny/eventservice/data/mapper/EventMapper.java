package ru.calendorny.eventservice.data.mapper;

import org.springframework.stereotype.Component;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import ru.calendorny.eventservice.dto.LabelDto;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.ParticipantDto;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventEntity toEntity (CreateEventRequest createEventRequest, List<EventLabelEntity> labels, UUID organizerId) {
        return EventEntity.builder()
            .title(createEventRequest.title())
            .description(createEventRequest.description())
            .location(createEventRequest.location())
            .start(createEventRequest.start())
            .end(createEventRequest.end())
            .rrule(createEventRequest.rrule())
            .isMeeting(createEventRequest.isMeeting())
            .meetingType(createEventRequest.meetingType())
            .labels(labels)
            .organizerId(organizerId)
            .isActive(true)
            .build();
    }

    public EventDetailedResponse toDetailedResponseWithoutReminders (EventEntity entity) {
        return buildBaseEventDetailedResponse(entity).build();
    }

    public EventDetailedResponse toDetailedResponseWithReminders (EventEntity entity, List<ReminderEntity> reminderEntities) {
        return buildBaseEventDetailedResponse(entity)
            .reminder(reminderToDto(reminderEntities))
            .build();
    }

    private EventDetailedResponse.EventDetailedResponseBuilder buildBaseEventDetailedResponse(EventEntity entity) {
        return EventDetailedResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .location(entity.getLocation())
            .startTime(entity.getStart())
            .endTime(entity.getEnd())
            .rrule(entity.getRrule())
            .labels(
                Optional.ofNullable(entity.getLabels())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(this::eventLabelToDto)
                    .collect(Collectors.toList())
            )
            .isMeeting(entity.isMeeting())
            .meetingType(entity.getMeetingType())
            .videoMeetingUrl(entity.getVideoMeetingUrl())
            .participantEmails(
                Optional.ofNullable(entity.getParticipants())
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(this::participantEntityToDto)
                    .collect(Collectors.toList())
            )
            .organizerId(entity.getOrganizerId());
    }


    LabelDto eventLabelToDto(EventLabelEntity entity) {
        return LabelDto.builder()
            .id(entity.getId())
            .name(entity.getName())
            .color(entity.getColor())
            .build();
    }

    ReminderDto reminderToDto(List<ReminderEntity> reminderEntities) {
        return ReminderDto.builder()
            .minutesBefore(reminderEntities.stream().map(ReminderEntity::getMinutesBefore).collect(Collectors.toList()))
            .build();
    }

    ParticipantDto participantEntityToDto(ParticipantEntity entity) {
        return ParticipantDto.builder()
            .userId(entity.getUserId())
            .email(entity.getEmail())
            .status(entity.getStatus())
            .build();
    }
}
