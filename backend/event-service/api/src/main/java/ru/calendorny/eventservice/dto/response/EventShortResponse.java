package ru.calendorny.eventservice.dto.response;

import lombok.Builder;
import ru.calendorny.eventservice.dto.LabelDto;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventShortResponse(
    Long id,
    String title,
    String location,
    LocalDateTime startTime,
    LocalDateTime endTime,
    List<LabelDto> labels
) {
}

