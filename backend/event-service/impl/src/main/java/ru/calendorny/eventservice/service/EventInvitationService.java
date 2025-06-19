package ru.calendorny.eventservice.service;

import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import java.util.List;
import java.util.UUID;

public interface EventInvitationService {

    List<EventDetailedResponse> getAllEventInvitations(UUID userId);

    EventDetailedResponse answerInvitation(UUID userId, Long eventId, ParticipantStatus participantStatus);
}
