package ru.calendorny.eventservice.dto;

import lombok.Builder;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import java.util.UUID;

@Builder
public record ParticipantDto(
    UUID userId,
    String email,
    ParticipantStatus status
) {
}
