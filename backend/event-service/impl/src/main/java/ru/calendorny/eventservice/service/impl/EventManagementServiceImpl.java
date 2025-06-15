package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
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
    public void updateEventInfoById(UUID userId, Long eventId, UpdateEventInfoRequest updateEventInfoRequest) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("Event with id: %s not found".formatted(eventId)));
        if (!userId.equals(event.getOrganizerId())) {
            throw new ForbiddenException("Events can be updated only by it's organizer");
        }
        event.setTitle(updateEventInfoRequest.title());
        event.setDescription(updateEventInfoRequest.description());
        event.setLocation(updateEventInfoRequest.location());
        event.setStart(updateEventInfoRequest.startTime());
        event.setEnd(updateEventInfoRequest.endTime());

        List<EventLabelEntity> newLabels = new ArrayList<>();
        for (Long labelId : updateEventInfoRequest.labels()) {
            EventLabelEntity eventLabelEntity = eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label with id: %s was not found".formatted(labelId)));
            newLabels.add(eventLabelEntity);
        }
        try {
            updateReminder(userId, eventId, updateEventInfoRequest, updateEventInfoRequest.startTime(), updateEventInfoRequest.endTime());
        } catch (SchedulerException e) {

        }
    }

    private void updateReminder(UUID userId, Long eventId, UpdateEventInfoRequest event, LocalDateTime start, LocalDateTime end) throws SchedulerException {
        List<ReminderEntity> reminders = reminderRepository.findByEventId(eventId);
        for (ReminderEntity reminder : reminders) {
            if (reminder.getNotificationJobId() != null) {
                try {
                    eventSchedulingService.deleteJob(reminder.getNotificationJobId());
                } catch (SchedulerException e) {
                }
            }

            LocalDateTime reminderTime = start.minusMinutes(reminder.getMinutesBefore());
            EventNotificationRequest request = new EventNotificationRequest(
                eventId,
                userId,
                event.title(),
                event.location(),
                start,
                end
            );
            String newJobId = eventSchedulingService.scheduleEvent(reminderTime, request, event.rrule());
            reminder.setNotificationJobId(newJobId);
            reminderRepository.save(reminder);
        }
    }



    @Override
    public void updateEventReminderById(UUID userId, Long eventId, UpdateEventReminderRequest updateEventReminderRequest) {
        // 1. Проверяем, что пользователь является участником события
        ParticipantEntity participant = participantRepository.findByUserIdAndEvent_Id(userId, eventId)
            .orElseThrow(() -> new NotFoundException("Participant not found for event"));

        // 2. Получаем текущие напоминания для этого события и пользователя
        List<ReminderEntity> existingReminders = reminderRepository.findByEventIdAndUserId(eventId, userId);

        // 3. Удаляем все старые напоминания и их jobs
        existingReminders.forEach(reminder -> {
            try {
                if (reminder.getNotificationJobId() != null) {
                    eventSchedulingService.deleteJob(reminder.getNotificationJobId());
                }
                reminderRepository.delete(reminder);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to delete reminder job", e);
            }
        });

        // 4. Создаем новые напоминания из запроса
        for (Integer minutesBefore : updateEventReminderRequest.reminder().minutesBefore()) {
            ReminderEntity newReminder = ReminderEntity.builder()
                .event(participant.getEvent())
                .minutesBefore(minutesBefore)
                .build();

            // Создаем job для напоминания
            createReminderJob(newReminder, participant.getEvent());

            // Сохраняем напоминание
            reminderRepository.save(newReminder);
        }
    }

    private void createReminderJob(ReminderEntity reminder, EventEntity event) {
        try {
            LocalDateTime reminderTime = event.getStart().minusMinutes(reminder.getMinutesBefore());

            EventNotificationRequest request = new EventNotificationRequest(
                event.getId(),
                event.getOrganizerId(),
                event.getTitle(),
                event.getLocation(),
                event.getStart(),
                event.getEnd()
            );

            String jobId = eventSchedulingService.scheduleEvent(reminderTime, request, event.getRrule());
            reminder.setNotificationJobId(jobId);
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to create reminder job", e);
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
