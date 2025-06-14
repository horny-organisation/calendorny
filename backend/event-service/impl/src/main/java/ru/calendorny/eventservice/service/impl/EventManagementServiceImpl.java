package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.mapper.EventMapper;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.exception.BadRequestException;
import ru.calendorny.eventservice.exception.ForbiddenException;
import ru.calendorny.eventservice.exception.NotFoundException;
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.quartz.service.JobSchedulerService;
import ru.calendorny.eventservice.repository.EventLabelRepository;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.repository.ParticipantRepository;
import ru.calendorny.eventservice.service.EventManagementService;
import ru.calendorny.eventservice.service.EventSchedulingService;
import ru.calendorny.eventservice.service.MeetingService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventManagementServiceImpl implements EventManagementService {

    private final EventRepository eventRepository;

    private final EventLabelRepository eventLabelRepository;

    private final ParticipantRepository participantRepository;

    private final EventMapper eventMapper;

    private final EventSchedulingService eventSchedulingService;

    private final MeetingService meetingService;

    @Override
    public EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest) {
        List<EventLabelEntity> eventLabels = new ArrayList<>();
        for (Long labelId : createEventRequest.labels()) {
            EventLabelEntity eventLabelEntity = eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label with id: %s was not found".formatted(labelId)));
            eventLabels.add(eventLabelEntity);
        }

        EventEntity newEvent = EventEntity.builder()
            .title(createEventRequest.title())
            .description(createEventRequest.description())
            .location(createEventRequest.location())
            .start(createEventRequest.startTime())
            .end(createEventRequest.endTime())
            .rrule(createEventRequest.rrule())
            .isMeeting(createEventRequest.isMeeting())
            .meetingType(createEventRequest.meetingType())
            .labels(eventLabels)
            .organizerId(userId)
            .isActive(true)
            .build();
        EventEntity savedEvent = eventRepository.save(newEvent);

        if (createEventRequest.isMeeting()) {
            meetingService.sendMeetingRequest(createEventRequest.meetingType(), savedEvent.getId(), createEventRequest.startTime());
        }

        List<String> participantEmails = createEventRequest.participantEmails();
        if (!participantEmails.isEmpty()) {
            for (String email : participantEmails) {
                //TODO: получение id по email
                UUID participantId = userId;
                ParticipantEntity participant = ParticipantEntity.builder()
                    .event(savedEvent)
                    .userId(participantId)
                    .email(email)
                    .invitedAt(LocalDateTime.now())
                    .status(ParticipantStatus.PENDING)
                    .build();
                participantRepository.save(participant);
            }
        }

        ReminderDto reminderDto = createEventRequest.reminder();

        for (Integer minutesBefore : reminderDto.minutesBefore()) {
            LocalDateTime remindTime = createEventRequest.startTime().minusMinutes(minutesBefore);
            EventNotificationRequest request = EventNotificationRequest.builder()
                .userId()
                .title(createEventRequest.title())
                .location(createEventRequest.location())
                .build();
            eventSchedulingService.scheduleEvent(remindTime, )
        }


        return eventMapper.toDetailedResponse(savedEvent );
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        return eventMapper.toDetailedResponse(event);
    }

    @Override
    public void updateEventById(UUID userId, Long eventId, UpdateEventRequest updateEventRequest) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event with id: %s not found".formatted(eventId)));
        if (!userId.equals(event.getOrganizerId())) {
            throw new ForbiddenException("Events can be updated only by it's organizer");
        }
        event.setTitle(updateEventRequest.title());
        event.setDescription(updateEventRequest.description());
        event.setLocation(updateEventRequest.location());

        List<EventLabelEntity> newLabels = new ArrayList<>();
        for (Long labelId : updateEventRequest.labels()) {
            EventLabelEntity eventLabelEntity = eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label with id: %s was not found".formatted(labelId)));
            newLabels.add(eventLabelEntity);
        }
        event.setLabels(newLabels);
        event.setStart(updateEventRequest.startTime());
        event.setEnd(updateEventRequest.endTime());


    }

    @Override
    public void deleteEventById(UUID userId, Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (userId.equals(event.getOrganizerId())) {
            event.setActive(false);
            eventRepository.save(event);
        }
    }

    @Override
    public void setVideoMeetingLinkToEvent(Long eventId, String link) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        event.setVideoMeetingUrl(link);
        eventRepository.save(event);
    }
}
