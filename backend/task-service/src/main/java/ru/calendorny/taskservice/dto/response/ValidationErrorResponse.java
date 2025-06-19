package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Builder;
import ru.calendorny.taskservice.dto.error.ValidationError;

@Builder
public record ValidationErrorResponse(
    int code,
    String exceptionName,
    String exceptionMessage,
    List<ValidationError> validationErrors
) {
}
