package ru.calendorny.eventservice.dto.error;

import lombok.Builder;

@Builder
public record ValidationError(
    String field,
    String message
) {
}
