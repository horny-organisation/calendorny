package ru.calendorny.exception;

public class ForbiddenException extends ServiceException{

    public ForbiddenException(String message) {
        super(message);
    }
}
