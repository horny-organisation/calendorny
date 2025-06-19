package ru.calendorny.eventservice.exception;

public class NotFoundException extends ServiceException{

    public NotFoundException(String message) {
        super(message);
    }
}
