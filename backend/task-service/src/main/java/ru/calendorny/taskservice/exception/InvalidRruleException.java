package ru.calendorny.taskservice.exception;

public class InvalidRruleException extends ServiceException {

    public InvalidRruleException(String message) {
        super("500", message);
    }
}
