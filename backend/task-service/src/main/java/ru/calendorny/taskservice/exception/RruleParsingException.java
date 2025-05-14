package ru.calendorny.taskservice.exception;

public class RruleParsingException extends ServiceException {

    public RruleParsingException(String message) {
        super("500", message);
    }
}
