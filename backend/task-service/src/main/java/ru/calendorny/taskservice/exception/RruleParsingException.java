package ru.calendorny.taskservice.exception;

public class RruleParsingException extends ServiceException{

    public RruleParsingException() {
        super("500", "Can not parse rrule");
    }
}
