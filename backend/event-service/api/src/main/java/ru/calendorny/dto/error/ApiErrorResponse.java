package ru.calendorny.dto.error;

import lombok.Builder;

@Builder
public record ApiErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage
) {
}
