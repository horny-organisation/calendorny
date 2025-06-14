package ru.calendorny.eventservice.service;

import ru.calendorny.dto.enums.ParticipantStatus;
import ru.calendorny.dto.response.EventDetailedResponse;
import java.util.List;
import java.util.UUID;

public interface EventInvitationService {

    List<EventDetailedResponse> getAllEventInvitations(UUID userId);

    EventDetailedResponse answerInvitation(UUID userId, UUID eventId, ParticipantStatus participantStatus);
}
