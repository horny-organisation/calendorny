package ru.calendorny.dto.response;

import lombok.Builder;
import ru.calendorny.dto.LabelDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record EventShortResponse(
    UUID id,
    String title,
    String location,
    LocalDateTime startTime,
    LocalDateTime endTime,
    List<LabelDto> labels
) {
}

