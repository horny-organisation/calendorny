package ru.calendorny.eventservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.data.mapper.EventMapper;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.response.EventDetailedResponse;
import ru.calendorny.eventservice.exception.BadRequestException;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.repository.ParticipantRepository;
import ru.calendorny.eventservice.service.EventInvitationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventInvitationServiceImpl implements EventInvitationService {

    private final EventRepository eventRepository;

    private final ParticipantRepository participantRepository;

    private final EventMapper eventMapper;

    @Override
    public List<EventDetailedResponse> getAllEventInvitations(UUID userId) {
        List<EventEntity> pendingEvents = eventRepository.getAllPendingToUserEvents(userId);
        return pendingEvents.stream()
            .map(eventMapper::toDetailedResponse)
            .collect(Collectors.toList());
    }

    @Override
    public EventDetailedResponse answerInvitation(UUID userId, Long eventId, ParticipantStatus participantStatus) {
        ParticipantEntity participant = participantRepository.findByUserIdAndEvent_Id(userId, eventId)
            .orElseThrow(() -> new BadRequestException("No participant found with id: %s".formatted(userId)));
        if (participantStatus == ParticipantStatus.ACCEPTED) {
            participant.setStatus(participantStatus);
            participant.setRespondedAt(LocalDateTime.now());
            participantRepository.save(participant);
        } else if (participantStatus == ParticipantStatus.REJECTED){
            participantRepository.deleteById(participant.getId());
        }
        EventEntity event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BadRequestException("No such event with id: %s".formatted(eventId)));
        return eventMapper.toDetailedResponse(event);
    }
}
