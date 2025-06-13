package ru.calendorny.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.dto.request.CreateEventRequest;
import ru.calendorny.dto.request.UpdateEventRequest;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.dto.response.EventShortResponse;
import ru.calendorny.security.AuthenticatedUser;
import ru.calendorny.service.EventManagementService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventManagementServiceImpl implements EventManagementService {

    @Override
    public EventDetailedResponse createEvent(UUID userId, CreateEventRequest createEventRequest) {
        return null;
    }

    @Override
    public List<EventShortResponse> getAllEventsByDateRange(UUID userId, LocalDate from, LocalDate to) {
        return null;
    }

    @Override
    public EventDetailedResponse getEventDetailedInfoById(UUID eventId) {
        return null;
    }

    @Override
    public void updateEventById(AuthenticatedUser authenticatedUser, UUID eventId, UpdateEventRequest updateEventRequest) {

    }

    @Override
    public void deleteEventById(UUID eventId) {

    }
}
