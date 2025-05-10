package ru.calendorny.taskservice.exception;


public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException() {
        super("Task not found");
    }
}
