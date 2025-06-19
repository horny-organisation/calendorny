package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.data.dto.EventInfo;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import ru.calendorny.eventservice.data.mapper.EventMapper;
import ru.calendorny.eventservice.dto.LabelDto;
import ru.calendorny.eventservice.dto.ParticipantDto;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.MeetingType;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventInfoRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventReminderRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.exception.BadRequestException;
import ru.calendorny.eventservice.exception.ForbiddenException;
import ru.calendorny.eventservice.exception.NotFoundException;
import ru.calendorny.eventservice.exception.ServiceException;
import ru.calendorny.eventservice.rabbit.dto.request.MeetingCreateRequest;
import ru.calendorny.eventservice.rabbit.producer.RabbitMeetingProducer;
import ru.calendorny.eventservice.repository.EventLabelRepository;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.repository.ParticipantRepository;
import ru.calendorny.eventservice.repository.ReminderRepository;
import ru.calendorny.eventservice.service.EventManagementService;
import ru.calendorny.eventservice.service.EventSchedulingService;
import ru.calendorny.eventservice.util.rrule.RruleEventCalculator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventManagementServiceImpl implements EventManagementService {

    private final EventRepository eventRepository;

    private final EventLabelRepository eventLabelRepository;

    private final ParticipantRepository participantRepository;

    private final ReminderRepository reminderRepository;

    private final EventMapper eventMapper;

    private final EventSchedulingService eventSchedulingService;

    private final RruleEventCalculator rruleEventCalculator;

    private final RabbitMeetingProducer meetingProducer;

    @Override
    public EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest) {
        List<EventLabelEntity> eventLabels = getEventLabelsEntitiesFromRequest(createEventRequest.labels());

        EventEntity newEvent = eventMapper.toEntity(createEventRequest, eventLabels, userId);
        EventEntity savedEvent = eventRepository.save(newEvent);

        if (createEventRequest.isMeeting()) {
            sendMeetingRequest(createEventRequest.meetingType(), savedEvent.getId(), createEventRequest.start());
        }

        List<ParticipantDto> participants = createEventRequest.participants();
        if (!participants.isEmpty()) {
            createParticipants(participants, savedEvent);
        }
        ReminderDto reminderDto = createEventRequest.reminder();
        createEventReminders(userId, reminderDto, savedEvent);
        List<ReminderEntity> savedReminders = reminderRepository.findAllByEvent_IdAndUserId(savedEvent.getId(), userId);
        return eventMapper.toDetailedResponseWithReminders(savedEvent, savedReminders);
    }

    private void sendMeetingRequest(MeetingType meetingType, Long eventId, LocalDateTime start) {
        MeetingCreateRequest request = MeetingCreateRequest.builder()
            .eventId(eventId)
            .startDateTime(start)
            .build();
        switch (meetingType) {
            case GOOGLE -> meetingProducer.sendGoogleMeetingCreationRequest(request);
            case ZOOM -> meetingProducer.sendZoomMeetingCreationRequest(request);
            default -> throw new ServiceException("No such meeting type: %s".formatted(meetingType));
        }
    }

    private List<EventLabelEntity> getEventLabelsEntitiesFromRequest(List<Long> eventLabelId) {
        List<EventLabelEntity> eventLabels = new ArrayList<>();
        for (Long labelId : eventLabelId) {
            EventLabelEntity eventLabelEntity = eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label with id: %s was not found".formatted(labelId)));
            eventLabels.add(eventLabelEntity);
        }
        return eventLabels;
    }

    private void createEventReminders(UUID userId, ReminderDto reminderDto, EventEntity eventEntity) {
        LocalDateTime start = eventEntity.getStart();
        LocalDateTime end = eventEntity.getEnd();
        RruleDto rruleDto = eventEntity.getRrule();
        EventInfo eventInfo = EventInfo.builder()
            .eventId(eventEntity.getId())
            .title(eventEntity.getTitle())
            .location(eventEntity.getLocation())
            .build();
        for (Integer minutesBefore : reminderDto.minutesBefore()) {
            try {
                UUID notificationJobId = eventSchedulingService.schedule(eventInfo, userId, rruleDto, start, end, minutesBefore);
                ReminderEntity newReminder = ReminderEntity.builder()
                    .userId(userId)
                    .event(eventEntity)
                    .notificationJobId(notificationJobId)
                    .minutesBefore(minutesBefore)
                    .build();
                reminderRepository.save(newReminder);
            } catch (SchedulerException e) {
                throw new ServiceException("Can't set reminder to event: %s".formatted(e.getMessage()));
            }
        }

    }

    private void createParticipants(List<ParticipantDto> participants, EventEntity event) {
        for (ParticipantDto participantDto : participants) {
            ParticipantEntity participant = ParticipantEntity.builder()
                .event(event)
                .userId(participantDto.userId())
                .email(participantDto.email())
                .invitedAt(LocalDateTime.now())
                .status(ParticipantStatus.PENDING)
                .build();
            participantRepository.save(participant);
        }
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDateTime from, LocalDateTime to) {
        List<EventEntity> simpleEvents = eventRepository.findAllSimpleEventsInRange(userId, from, to);
        List<EventShortResponse> result = new ArrayList<>(simpleEvents.stream()
            .map(eventMapper::toShortResponse)
            .toList());
        List<EventEntity> recurEvents = eventRepository.findAllRecurEventsInRange(userId);
        for (EventEntity recurEvent :recurEvents) {
            result.addAll(rruleEventCalculator.generateOccurrences(eventMapper.toDetailedResponseWithoutReminders(recurEvent), from, to));
        }
        return result;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(UUID userId, Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (!event.isActive()) {
            throw new NotFoundException("Event with id: %s not found".formatted(eventId));
        }
        List<ReminderEntity> savedReminders = reminderRepository.findAllByEvent_IdAndUserId(eventId, userId);
        return eventMapper.toDetailedResponseWithReminders(event, savedReminders);
    }

    @Override
    public void deleteEventById(UUID userId, Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (!userId.equals(event.getOrganizerId())) {
            throw new ForbiddenException("Only organizer can delete events");
        }
        List<ReminderEntity> reminders = reminderRepository.findAllByEvent_Id(eventId);
        for (ReminderEntity reminder : reminders){
            try {
                eventSchedulingService.deleteJob(reminder.getNotificationJobId());
            } catch (SchedulerException e) {
                throw new ServiceException("Can't delete event notification: %s".formatted(e.getMessage()));
            }
        }
        event.setActive(false);
        eventRepository.save(event);
    }

    @Override
    public void updateEventInfo(UUID userId, Long eventId, UpdateEventInfoRequest updateEventInfoRequest) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (!event.isActive()) {
            throw new NotFoundException("Event with id: %s not found".formatted(eventId));
        }
        if (!userId.equals(event.getOrganizerId())) {
            throw new ForbiddenException("Only organizer can update event main info");
        }
        List<ReminderEntity> oldReminders = reminderRepository.findAllByEvent_Id(eventId);
        for (ReminderEntity oldReminder : oldReminders) {
            try {
                eventSchedulingService.deleteJob(oldReminder.getNotificationJobId());
            } catch (SchedulerException e) {
                throw new ServiceException("Can't delete event notification: %s".formatted(e.getMessage()));
            }
        }

        event.setTitle(updateEventInfoRequest.title());
        event.setDescription(updateEventInfoRequest.description());
        event.setLocation(updateEventInfoRequest.location());
        event.setStart(updateEventInfoRequest.start());
        event.setEnd(updateEventInfoRequest.end());
        event.setRrule(updateEventInfoRequest.rrule());
        event.setLabels(getEventLabelsEntitiesFromRequest(updateEventInfoRequest.labels()));
        if (!event.isMeeting() && updateEventInfoRequest.isMeeting()) {
            sendMeetingRequest(updateEventInfoRequest.meetingType(), eventId, updateEventInfoRequest.start());
        } else if (event.isMeeting()) {
            if (!updateEventInfoRequest.isMeeting()) {
                event.setMeeting(false);
                event.setMeetingType(MeetingType.NONE);
                event.setVideoMeetingUrl(null);
            }
            else {
                if (event.getMeetingType() != updateEventInfoRequest.meetingType()) {
                    event.setMeetingType(updateEventInfoRequest.meetingType());
                    event.setVideoMeetingUrl(null);
                    sendMeetingRequest(updateEventInfoRequest.meetingType(), eventId, updateEventInfoRequest.start());
                }
            }
        }
        List<UUID> oldParticipants = event.getParticipants().stream().map(ParticipantEntity::getUserId).toList();
        List<UUID> newParticipants = updateEventInfoRequest.participants().stream().map(ParticipantDto::userId).toList();
        event.setParticipants(updateEventInfoRequest.participants().stream().map(p -> ParticipantEntity.builder()
            .event(event)
            .userId(p.userId())
            .status(p.status())
            .build()
        ).collect(Collectors.toList()));
        EventEntity savedEvent = eventRepository.save(event);

        List<UUID> deletedParticipants = oldParticipants.stream().filter(id -> !newParticipants.contains(id)).toList();
        for (UUID participant : deletedParticipants) {
            participantRepository.deleteByUserId(participant);
        }
        List<UUID> onlyNewParticipants = newParticipants.stream().filter(id -> !oldParticipants.contains(id)).toList();
        createParticipants(updateEventInfoRequest.participants().stream().filter(p -> onlyNewParticipants.contains(p.userId())).collect(Collectors.toList()), savedEvent);
        List<UUID> survivorParticipants = oldParticipants.stream().filter(newParticipants::contains).toList();
        for (UUID participant : survivorParticipants) {
            ReminderDto reminder = ReminderDto.builder()
                .minutesBefore(oldReminders.stream().filter(r -> r.getUserId().equals(participant)).map(ReminderEntity::getMinutesBefore).collect(Collectors.toList()))
                .build();
            createEventReminders(userId, reminder, savedEvent);
        }
    }

    @Override
    public void updateEventReminder(UUID userId, Long eventId, UpdateEventReminderRequest updateEventReminderRequest) {
        List<ReminderEntity> oldReminders = reminderRepository.findAllByEvent_Id(eventId);
        for (ReminderEntity oldReminder : oldReminders) {
            try {
                eventSchedulingService.deleteJob(oldReminder.getNotificationJobId());
            } catch (SchedulerException e) {
                throw new ServiceException("Can't delete event notification: %s".formatted(e.getMessage()));
            }
        }
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (!event.isActive()) {
            throw new NotFoundException("Event with id: %s not found".formatted(eventId));
        }
        createEventReminders(userId, updateEventReminderRequest.reminder(), event);
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return eventLabelRepository.findAll().stream().map(eventMapper::eventLabelToDto).collect(Collectors.toList());
    }
}
