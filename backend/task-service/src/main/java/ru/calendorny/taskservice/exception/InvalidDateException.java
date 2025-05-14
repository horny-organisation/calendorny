package ru.calendorny.taskservice.exception;

public class InvalidDateException extends ServiceException {

    public InvalidDateException(String message) {
        super("500", message);
    }
}
