package ru.calendorny.eventservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.calendorny.eventservice.dto.error.ApiErrorResponse;
import ru.calendorny.eventservice.dto.error.ValidationError;
import ru.calendorny.eventservice.dto.error.ValidationErrorResponse;
import ru.calendorny.eventservice.exception.ForbiddenException;
import ru.calendorny.eventservice.exception.NotFoundException;
import ru.calendorny.eventservice.exception.ServiceException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, WebRequest webRequest) {

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
        NotFoundException ex, WebRequest webRequest) {

        log.error("""
                Event not found:
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

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbiddenException(
        ForbiddenException ex, WebRequest webRequest) {

        log.error("""
                Forbidden:
                {}
                Error: {}
                Stack trace: {}""",
            webRequest.getDescription(false),
            ex.getMessage(),
            getFormattedStackTrace(ex));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .code(HttpStatus.FORBIDDEN.value())
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiErrorResponse);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceException(
        ForbiddenException ex, WebRequest webRequest) {

        log.error("""
                Service:
                {}
                Error: {}
                Stack trace: {}""",
            webRequest.getDescription(false),
            ex.getMessage(),
            getFormattedStackTrace(ex));

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    private String getFormattedStackTrace(Throwable ex) {
        return Arrays.stream(ex.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n\tat "));
    }
}
