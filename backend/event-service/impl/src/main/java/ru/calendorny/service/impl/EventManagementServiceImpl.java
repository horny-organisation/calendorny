package ru.calendorny.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.data.entity.EventEntity;
import ru.calendorny.data.entity.EventLabelEntity;
import ru.calendorny.data.entity.ParticipantEntity;
import ru.calendorny.data.mapper.EventMapper;
import ru.calendorny.dto.enums.ParticipantStatus;
import ru.calendorny.dto.request.CreateEventRequest;
import ru.calendorny.dto.request.UpdateEventRequest;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.exception.BadRequestException;
import ru.calendorny.exception.ForbiddenException;
import ru.calendorny.exception.NotFoundException;
import ru.calendorny.quartz.JobSchedulerService;
import ru.calendorny.repository.EventLabelRepository;
import ru.calendorny.repository.EventRepository;
import ru.calendorny.repository.ParticipantRepository;
import ru.calendorny.security.AuthenticatedUser;
import ru.calendorny.service.EventManagementService;
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

    private final JobSchedulerService jobSchedulerService;

    @Override
    public EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest) {
        List<EventLabelEntity> eventLabels = new ArrayList<>();
        for (Long labelId : createEventRequest.labels()) {
            EventLabelEntity eventLabelEntity = eventLabelRepository.findById(labelId)
                .orElseThrow(() -> new BadRequestException("Label with id: %s was not found".formatted(labelId)));
            eventLabels.add(eventLabelEntity);
        }

        if (createEventRequest.isMeeting()){
            //TODO: получение ссылки на видел конфу по meetingType
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
        //TODO: запланировать отправку уведомлений

        return eventMapper.toDetailedResponse(savedEvent );
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(UUID eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        return eventMapper.toDetailedResponse(event);
    }

    @Override
    public void updateEventById(UUID userId, UUID eventId, UpdateEventRequest updateEventRequest) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
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
    public void deleteEventById(UUID userId, UUID eventId) {
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new NotFoundException("Event with id: %s not found".formatted(eventId)));
        if (userId.equals(event.getOrganizerId())) {
            event.setActive(false);
            eventRepository.save(event);
        }
    }
}
