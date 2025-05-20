package ru.calendorny.taskservice.exception;

import java.util.UUID;

public class TaskNotFoundException extends NotFoundException {
    public TaskNotFoundException(UUID taskId) {
        super("Task with id: %s not found".formatted(taskId));
    }
}
