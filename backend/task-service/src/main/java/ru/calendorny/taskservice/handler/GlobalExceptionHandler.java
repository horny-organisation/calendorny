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
import ru.calendorny.taskservice.dto.response.ValidationError;
import ru.calendorny.taskservice.dto.response.ValidationErrorResponse;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.TaskProcessorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, WebRequest request) {

        List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build())
            .toList();

        log.error("""
                Validation failed:
                {}
                Errors: {}
                Stack trace: {}""",
            request.getDescription(false),
            validationErrors,
            getFormattedStackTrace(ex));

        ValidationErrorResponse response = ValidationErrorResponse.builder()
            .description("Validation failed for " + request.getDescription(false))
            .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage("Invalid request data")
            .validationErrors(validationErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTaskNotFoundException(
        TaskNotFoundException ex, WebRequest request) {

        log.error("""
                Task not found:
                {}
                Error: {}
                Stack trace: {}""",
            request.getDescription(false),
            ex.getMessage(),
            getFormattedStackTrace(ex));

        ApiErrorResponse response = ApiErrorResponse.builder()
            .description("Task not found")
            .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TaskProcessorException.class)
    public ResponseEntity<ApiErrorResponse> handleTaskProcessorException(
        TaskProcessorException ex, WebRequest request) {

        log.error("""
                Task processing failed:
                {}
                Error: {}
                Root cause: {}
                Stack trace: {}""",
            request.getDescription(false),
            ex.getMessage(),
            ex.getCause() != null ? ex.getCause().getMessage() : "none",
            getFormattedStackTrace(ex));

        ApiErrorResponse response = ApiErrorResponse.builder()
            .description("Task processing error")
            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            .description("Internal server error")
            .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
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
