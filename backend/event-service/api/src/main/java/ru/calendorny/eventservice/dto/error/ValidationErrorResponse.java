package ru.calendorny.eventservice.dto.error;

import java.util.List;
import lombok.Builder;

@Builder
public record ValidationErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage,
    List<ValidationError> validationErrors
) {
}
