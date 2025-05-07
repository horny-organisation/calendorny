package ru.calendorny.taskservice.dto.response;

import java.util.List;
import lombok.Data;
import ru.calendorny.taskservice.util.ValidationError;

@Data
public class ValidationErrorResponse {

    String description;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stacktrace;

    List<ValidationError> validationErrors;
}
