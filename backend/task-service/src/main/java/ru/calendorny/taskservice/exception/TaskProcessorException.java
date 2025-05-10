package ru.calendorny.taskservice.exception;

public class TaskProcessorException extends ServiceException{

    public TaskProcessorException() {
        super("500", "Error during choosing task processor");
    }
}
