package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import ru.calendorny.eventservice.data.mapper.EventMapper;
import ru.calendorny.eventservice.dto.ReminderDto;
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
import ru.calendorny.eventservice.kafka.dto.request.EventNotificationRequest;
import ru.calendorny.eventservice.repository.EventLabelRepository;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.repository.ParticipantRepository;
import ru.calendorny.eventservice.repository.ReminderRepository;
import ru.calendorny.eventservice.service.EventManagementService;
import ru.calendorny.eventservice.service.EventSchedulingService;
import ru.calendorny.eventservice.service.MeetingService;
import ru.calendorny.eventservice.util.rrule.RruleEventCalculator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventManagementServiceImpl implements EventManagementService {

    private final EventRepository eventRepository;

    private final EventLabelRepository eventLabelRepository;

    private final ParticipantRepository participantRepository;

    private final ReminderRepository reminderRepository;

    private final EventMapper eventMapper;

    private final EventSchedulingService eventSchedulingService;

    private final MeetingService meetingService;

    private final RruleEventCalculator rruleEventCalculator;

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
                .userId(userId)
                .title(createEventRequest.title())
                .location(createEventRequest.location())
                .build();
            try {
                eventSchedulingService.scheduleEvent(remindTime, request, createEventRequest.rrule());
            } catch (SchedulerException e) {
                throw new ServiceException("We can't schedule a reminder");
            }
        }
        return eventMapper.toDetailedResponse(savedEvent );
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDateTime from, LocalDateTime to) {
        List<EventEntity> events = eventRepository.findRelevantEvents(userId, from, to);
        List<EventShortResponse> result = new ArrayList<>();
        for (EventEntity event : events) {
            if (event.getRrule() == null) {
                result.add(eventMapper.toShortResponse(event));
            } else {
                try {
                    result.addAll(rruleEventCalculator.generateOccurrences(eventMapper.toDetailedResponse(event), from, to));
                } catch (InvalidRecurrenceRuleException e) {
                    throw new ServiceException("Can not get recurrency events from rrule: %s".formatted(event.getRrule()));
                }
            }
        }
        result.sort(Comparator.comparing(EventShortResponse::startTime));
        return result;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        return eventMapper.toDetailedResponse(event);
    }

    @Override
    @Transactional
    public void updateEventInfoById(UUID userId, Long eventId, UpdateEventInfoRequest request) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event not found"));

        if (!userId.equals(event.getOrganizerId())) {
            throw new ForbiddenException("Only organizer can update event info");
        }

        if (request.startTime().isAfter(request.endTime())) {
            throw new BadRequestException("End time must be after start time");
        }
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStart(request.startTime());
        event.setEnd(request.endTime());
        event.setRrule(request.rrule());
        event.setMeeting(request.isMeeting());
        event.setMeetingType(request.meetingType());
        eventRepository.save(event);
        updateEventLabels(event, request.labels());
        if (request.participantEmails() != null) {
            updateParticipants(event, request.participantEmails());
        }
        updateOrganizerReminders(event, request.startTime());
    }

    private void updateEventLabels(EventEntity event, List<Long> labelIds) {
        if (labelIds == null) return;

        List<EventLabelEntity> newLabels = labelIds.stream()
            .map(labelId -> eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label not found")))
            .toList();

        event.setLabels(newLabels);
    }

    private void updateParticipants(EventEntity event, List<String> participantEmails) {
        List<ParticipantEntity> currentParticipants = participantRepository.findByEvent_Id(event.getId());
        currentParticipants.stream()
            .filter(p -> !participantEmails.contains(p.getEmail()))
            .forEach(participantRepository::delete);

        participantEmails.forEach(email -> {
            if (currentParticipants.stream().noneMatch(p -> p.getEmail().equals(email))) {
                // TODO: Получить userId по email
                UUID participantId = UUID.randomUUID();

                ParticipantEntity participant = ParticipantEntity.builder()
                    .event(event)
                    .userId(participantId)
                    .email(email)
                    .status(ParticipantStatus.PENDING)
                    .invitedAt(LocalDateTime.now())
                    .build();

                participantRepository.save(participant);
            }
        });
    }

    private void updateOrganizerReminders(EventEntity event, LocalDateTime newStartTime) {
        List<ReminderEntity> organizerReminders = reminderRepository.findByEventIdAndUserId(
            event.getId(),
            event.getOrganizerId()
        );

        for (ReminderEntity reminder : organizerReminders) {
            try {
                if (reminder.getNotificationJobId() != null) {
                    eventSchedulingService.deleteJob(reminder.getNotificationJobId());
                }
                LocalDateTime reminderTime = newStartTime.minusMinutes(reminder.getMinutesBefore());
                EventNotificationRequest notification = createNotificationRequest(event, event.getOrganizerId());

                String newJobId = eventSchedulingService.scheduleEvent(
                    reminderTime,
                    notification,
                    event.getRrule()
                );

                reminder.setNotificationJobId(newJobId);
                reminderRepository.save(reminder);

            } catch (SchedulerException e) {
                throw new ServiceException("Failed to update organizer reminder");
            }
        }
    }

    private EventNotificationRequest createNotificationRequest(EventEntity event, UUID userId) {
        return EventNotificationRequest.builder()
            .eventId(event.getId())
            .userId(userId)
            .title(event.getTitle())
            .location(event.getLocation())
            .start(event.getStart())
            .end(event.getEnd())
            .build();
    }
    private void scheduleReminderJob(ReminderEntity reminder, EventEntity event) {
        try {
            LocalDateTime reminderTime = event.getStart().minusMinutes(reminder.getMinutesBefore());

            EventNotificationRequest notificationRequest = EventNotificationRequest.builder()
                .eventId(event.getId())
                .userId(reminder.getUserId())
                .title(event.getTitle())
                .location(event.getLocation())
                .start(event.getStart())
                .end(event.getEnd())
                .build();

            String jobId = eventSchedulingService.scheduleEvent(
                reminderTime,
                notificationRequest,
                event.getRrule()
            );
            reminder.setNotificationJobId(jobId);
        } catch (SchedulerException e) {
            throw new ServiceException("Failed to schedule reminder for event: " + event.getId());
        }
    }

    @Override
    @Transactional
    public void updateEventReminderById(UUID userId, Long eventId, UpdateEventReminderRequest request) {
        ParticipantEntity participant = participantRepository.findByUserIdAndEvent_Id(userId, eventId)
            .orElseThrow(() -> new ForbiddenException("You are not a participant of this event"));

        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event not found"));

        List<ReminderEntity> existingReminders = reminderRepository.findByEventIdAndUserId(eventId, userId);
        deleteRemindersWithJobs(existingReminders);

        if (request.reminder() != null && request.reminder().minutesBefore() != null) {
            for (Integer minutesBefore : request.reminder().minutesBefore()) {
                ReminderEntity newReminder = ReminderEntity.builder()
                    .event(event)
                    .userId(userId)
                    .minutesBefore(minutesBefore)
                    .build();

                scheduleReminderJob(newReminder, event);
                reminderRepository.save(newReminder);
            }
        }
    }

    private void deleteRemindersWithJobs(List<ReminderEntity> reminders) {
        for (ReminderEntity reminder : reminders) {
            try {
                if (reminder.getNotificationJobId() != null) {
                    eventSchedulingService.deleteJob(reminder.getNotificationJobId());
                }
                reminderRepository.delete(reminder);
            } catch (SchedulerException e) {
                throw new ServiceException("Failed to delete reminder job");
            }
        }
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
