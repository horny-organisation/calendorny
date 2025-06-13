package ru.calendorny.dto.response;

import lombok.Builder;
import ru.calendorny.dto.LabelDto;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record EventShortResponse(
    UUID id,
    UUID userId,
    String title,
    String location,
    LocalDateTime startTime,
    LocalDateTime endTime,
    LabelDto label
) {
}

