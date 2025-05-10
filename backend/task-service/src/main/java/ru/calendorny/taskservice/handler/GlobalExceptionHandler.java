package ru.calendorny.taskservice.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.calendorny.taskservice.dto.response.ApiErrorResponse;
import ru.calendorny.taskservice.dto.response.ValidationErrorResponse;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.UnauthorizedAccessException;
import ru.calendorny.taskservice.util.ValidationError;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build()
            )
            .toList();

        ValidationErrorResponse validationErrorResponse  = ValidationErrorResponse.builder()
            .description("Validation failed")
            .code("400")
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .stacktrace(getStackTrace(ex))
            .validationErrors(validationErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(TaskNotFoundException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .description("Not found error")
            .code("404")
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .stackTrace(getStackTrace(ex))
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiErrorResponse);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(UnauthorizedAccessException ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .description("Unauthorized")
            .code("401")
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .stackTrace(getStackTrace(ex))
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiErrorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.builder()
            .description("Internal server error")
            .code("500")
            .exceptionName(ex.getClass().getSimpleName())
            .exceptionMessage(ex.getMessage())
            .stackTrace(getStackTrace(ex))
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    private List<String> getStackTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.toList());
    }
}
