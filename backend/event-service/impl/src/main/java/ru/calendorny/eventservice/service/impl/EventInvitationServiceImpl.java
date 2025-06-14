package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.dto.enums.ParticipantStatus;
import ru.calendorny.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.service.EventInvitationService;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventInvitationServiceImpl implements EventInvitationService {

    @Override
    public List<EventDetailedResponse> getAllEventInvitations(UUID userId) {
        return null;
    }

    @Override
    public EventDetailedResponse answerInvitation(UUID userId, UUID eventId, ParticipantStatus participantStatus) {
        return null;
    }
}
