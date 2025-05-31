package ru.calendorny.taskservice.exception;

public class TaskProcessorException extends ServiceException {

    public TaskProcessorException() {
        super("Error during choosing task processor");
    }
}
