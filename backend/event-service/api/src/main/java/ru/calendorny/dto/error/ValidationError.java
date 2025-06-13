package ru.calendorny.dto.error;

import lombok.Builder;

@Builder
public record ValidationError(
    String field,
    String message
) {
}
