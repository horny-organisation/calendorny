package ru.calendorny.taskservice.exception;

public class NotFoundException extends ServiceException {

    public NotFoundException(String message) {
        super(message);
    }
}
