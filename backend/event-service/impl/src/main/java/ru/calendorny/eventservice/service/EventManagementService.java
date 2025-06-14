package ru.calendorny.eventservice.service;

import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.request.UpdateEventRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.eventservice.security.AuthenticatedUser;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EventManagementService {

    EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest);

    List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDate from, LocalDate to);

    EventDetailedResponse getEventDetailedInfoById(Long eventId);

    void updateEventById(UUID userId, Long eventId, UpdateEventRequest updateEventRequest);

    void deleteEventById(UUID userId, Long eventId);

    void setVideoMeetingLinkToEvent(Long eventId, String link);
}
