package ru.calendorny.authservice.dto.response;

import java.util.List;

public record ValidationErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage,
    List<ValidationError> validationErrors
) {
    public record ValidationError(
        String field,
        String message
    ) {
    }
}
