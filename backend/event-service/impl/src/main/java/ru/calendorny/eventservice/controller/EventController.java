package ru.calendorny.eventservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.api.EventApi;
import ru.calendorny.dto.enums.ParticipantStatus;
import ru.calendorny.dto.request.CreateEventRequest;
import ru.calendorny.dto.request.UpdateEventRequest;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.eventservice.security.AuthenticatedUser;
import ru.calendorny.eventservice.service.EventInvitationService;
import ru.calendorny.eventservice.service.EventManagementService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
    public EventDetailedResponse getEventDetailedInfoById(AuthenticatedUser authenticatedUser, UUID eventId) {
        return eventManagementService.getEventDetailedInfoById(eventId);
    }

    @Override
    public void updateEventById(AuthenticatedUser authenticatedUser, UUID eventId, UpdateEventRequest updateEventRequest) {
        eventManagementService.updateEventById(authenticatedUser, eventId, updateEventRequest);
    }

    @Override
    public void deleteEventById(AuthenticatedUser authenticatedUser, UUID eventId) {
        eventManagementService.deleteEventById(eventId);
    }

    @Override
    public List<EventDetailedResponse> getAllEventInvitations(AuthenticatedUser authenticatedUser) {
        return eventInvitationService.getAllEventInvitations(authenticatedUser.id());
    }

    @Override
    public EventDetailedResponse answerInvitation(AuthenticatedUser authenticatedUser, UUID eventId, ParticipantStatus participantStatus) {
        return eventInvitationService.answerInvitation(authenticatedUser.id(), eventId, participantStatus);
    }
}
