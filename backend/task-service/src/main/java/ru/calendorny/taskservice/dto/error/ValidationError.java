package ru.calendorny.taskservice.dto.error;

import lombok.Builder;

@Builder
public record ValidationError(
    String field,
    String message
) {
}
