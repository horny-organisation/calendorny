package ru.calendorny.taskservice.exception;

public class UnauthorizedAccessException extends ServiceException {
    public UnauthorizedAccessException() {
        super("401", "Unauthorized access");
    }
}
