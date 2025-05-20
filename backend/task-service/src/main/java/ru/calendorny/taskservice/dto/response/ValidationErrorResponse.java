package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ValidationErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage,
    List<ValidationError> validationErrors
) {
    @Builder
    public record ValidationError(
        String field,
        String message
    ) {
    }
}
