package ru.calendorny.taskservice.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServiceException extends RuntimeException {

    private String message;
}
