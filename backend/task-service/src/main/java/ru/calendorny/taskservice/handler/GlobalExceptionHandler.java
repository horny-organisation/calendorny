package ru.calendorny.taskservice.handler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.calendorny.taskservice.dto.response.ApiErrorResponse;
import ru.calendorny.taskservice.dto.response.ValidationErrorResponse;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.TaskProcessorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, WebRequest webRequest) {

        List<ValidationErrorResponse.ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> ValidationErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build())
            .toList();

        log.error("""
                Validation failed:
                {}
                Errors: {}
                Stack trace: {}""",
            webRequest.getDescription(false),
            validationErrors,
            getFormattedStackTrace(ex));

        ValidationErrorResponse validationErrorResponse = ValidationErrorResponse.builder()
            .code(HttpStatus.BAD_REQUEST.value())
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .validationErrors(validationErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTaskNotFoundException(
        TaskNotFoundException ex, WebRequest webRequest) {

        log.error("""
                Task not found:
                {}
                Error: {}
                Stack trace: {}""",
            webRequest.getDescription(false),
            ex.getMessage(),
            getFormattedStackTrace(ex));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .code(HttpStatus.NOT_FOUND.value())
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
        Exception ex, WebRequest request) {

        log.error("""
                Unexpected error occurred:
                {}
                Error type: {}
                Error message: {}
                Stack trace: {}""",
            request.getDescription(false),
            ex.getClass().getName(),
            ex.getMessage(),
            getFormattedStackTrace(ex));

        ApiErrorResponse response = ApiErrorResponse.builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getFormattedStackTrace(Throwable ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n\tat "));
    }
}
