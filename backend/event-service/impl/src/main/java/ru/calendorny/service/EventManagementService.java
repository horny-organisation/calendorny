package ru.calendorny.service;

import ru.calendorny.dto.request.CreateEventRequest;
import ru.calendorny.dto.request.UpdateEventRequest;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.security.AuthenticatedUser;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventManagementService {

    EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest);

    List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDate from, LocalDate to);

    EventDetailedResponse getEventDetailedInfoById(UUID eventId);

    void updateEventById(AuthenticatedUser authenticatedUser, UUID eventId, UpdateEventRequest updateEventRequest);

    void deleteEventById(UUID eventId);

    void setVideoMeetingLinkToEvent(Long eventId, String link);
}
