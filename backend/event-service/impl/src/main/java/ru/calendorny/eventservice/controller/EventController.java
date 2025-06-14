package ru.calendorny.eventservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.eventservice.api.EventApi;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.security.AuthenticatedUser;
import ru.calendorny.eventservice.service.EventInvitationService;
import ru.calendorny.eventservice.service.EventManagementService;
import java.time.LocalDate;
import java.util.List;

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
    public List<EventShortResponse> getAllEventsByDateRange(AuthenticatedUser authenticatedUser, LocalDate from, LocalDate to) {
        return eventManagementService.getAllEventsByDateRange(authenticatedUser.id(), from, to);
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(AuthenticatedUser authenticatedUser, Long eventId) {
        return eventManagementService.getEventDetailedInfoById(eventId);
    }

    @Override
    public void updateEventById(AuthenticatedUser authenticatedUser, Long eventId, UpdateEventRequest updateEventRequest) {
        eventManagementService.updateEventById(authenticatedUser.id(), eventId, updateEventRequest);
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
