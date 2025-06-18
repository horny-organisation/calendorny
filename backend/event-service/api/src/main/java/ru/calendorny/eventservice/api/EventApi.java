package ru.calendorny.eventservice.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.CreateEventRequest;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.dto.response.EventShortResponse;
import ru.calendorny.securitystarter.AuthenticatedUser;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/events")
public interface EventApi {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    EventDetailedResponse createEvent(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @Valid @RequestBody CreateEventRequest createEventRequest
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    List<EventShortResponse> getAllEventsByDateRange(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam LocalDateTime from,
        @RequestParam LocalDateTime to
    );

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    EventDetailedResponse getEventDetailedInfoById(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable("eventId") Long eventId
    );

    /*
    @PutMapping("/{eventId}/info")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    void updateEventInfoById(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable("eventId") Long eventId,
        @Valid @RequestBody UpdateEventInfoRequest updateEventInfoRequest
    );

    @PutMapping("/{eventId}/reminder")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    void updateEventReminderById(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable("eventId") Long eventId,
        @Valid @RequestBody UpdateEventReminderRequest updateEventReminderRequest
    );
     */

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    void deleteEventById(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable("eventId") Long eventId
    );

    @GetMapping("/invitations")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    List<EventDetailedResponse> getAllEventInvitations(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    );

    @PostMapping("/invitations/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    EventDetailedResponse answerInvitation(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @PathVariable("eventId") Long eventId,
        @RequestParam("answer") ParticipantStatus participantStatus
    );

}
