package ru.calendorny.eventservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.eventservice.api.EventApi;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventInfoRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventReminderRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.service.EventInvitationService;
import ru.calendorny.eventservice.service.EventManagementService;
import ru.calendorny.securitystarter.AuthenticatedUser;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController implements EventApi {

    private final EventManagementService eventManagementService;

    private final EventInvitationService eventInvitationService;

    @Override
    public EventDetailedResponse createEvent(AuthenticatedUser authenticatedUser, CreateEventRequest createEventRequest) {
        return eventManagementService.createEvent(authenticatedUser.id(), createEventRequest);
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(AuthenticatedUser authenticatedUser, LocalDateTime from, LocalDateTime to) {
        return eventManagementService.getAllEventsByDateRange(authenticatedUser.id(), from, to);
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(AuthenticatedUser authenticatedUser, Long eventId) {
        log.info("CONTROLLER: {}", authenticatedUser.id());
        return eventManagementService.getEventDetailedInfoById(authenticatedUser.id(), eventId);
    }

    @Override
    public void updateEventInfoById(AuthenticatedUser authenticatedUser, Long eventId, UpdateEventInfoRequest updateEventInfoRequest) {
        eventManagementService.updateEventInfo(authenticatedUser.id(), eventId, updateEventInfoRequest);
    }

    @Override
    public void updateEventReminderById(AuthenticatedUser authenticatedUser, Long eventId, UpdateEventReminderRequest updateEventReminderRequest) {
        eventManagementService.updateEventReminder(authenticatedUser.id(), eventId, updateEventReminderRequest);
    }

    @Override
    public void deleteEventById(AuthenticatedUser authenticatedUser, Long eventId) {
        eventManagementService.deleteEventById(authenticatedUser.id(), eventId);
    }

    @Override
    public List<EventDetailedResponse> getAllEventInvitations(AuthenticatedUser authenticatedUser) {
        return eventInvitationService.getAllEventInvitations(authenticatedUser.id());
    }

    @Override
    public EventDetailedResponse answerInvitation(AuthenticatedUser authenticatedUser, Long eventId, ParticipantStatus participantStatus) {
        return eventInvitationService.answerInvitation(authenticatedUser.id(), eventId, participantStatus);
    }
}
