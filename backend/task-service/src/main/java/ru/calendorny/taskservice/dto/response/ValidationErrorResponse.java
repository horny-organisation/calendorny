package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import ru.calendorny.taskservice.util.ValidationError;

@Data
@Builder
public class ValidationErrorResponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stacktrace;

    List<ValidationError> validationErrors;
}
