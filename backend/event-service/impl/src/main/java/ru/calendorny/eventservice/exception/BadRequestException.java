package ru.calendorny.eventservice.exception;

public class BadRequestException extends ServiceException{

    public BadRequestException(String message) {
        super(message);
    }
}
