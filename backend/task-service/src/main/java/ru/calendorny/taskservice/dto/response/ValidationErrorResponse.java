package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public class ValidationErrorResponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stacktrace;

    List<ValidationError> validationErrors;
}
