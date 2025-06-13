package ru.calendorny.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.calendorny.api.EventApi;
import ru.calendorny.dto.enums.ParticipantStatus;
import ru.calendorny.dto.request.CreateEventRequest;
import ru.calendorny.dto.request.UpdateEventRequest;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.security.AuthenticatedUser;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class EventController implements EventApi {
    @Override
    public EventDetailedResponse createEvent(AuthenticatedUser authenticatedUser, CreateEventRequest createEventRequest) {
        return null;
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(AuthenticatedUser authenticatedUser, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(AuthenticatedUser authenticatedUser, UUID eventId) {
        return null;
    }

    @Override
    public void updateEventById(AuthenticatedUser authenticatedUser, UUID eventId, UpdateEventRequest updateEventRequest) {

    }

    @Override
    public void deleteEventById(AuthenticatedUser authenticatedUser, UUID eventId) {

    }

    @Override
    public EventDetailedResponse getAllEventInvitations(AuthenticatedUser authenticatedUser) {
        return null;
    }

    @Override
    public EventDetailedResponse answerInvitation(AuthenticatedUser authenticatedUser, UUID eventId, ParticipantStatus participantStatus) {
        return null;
    }
}
