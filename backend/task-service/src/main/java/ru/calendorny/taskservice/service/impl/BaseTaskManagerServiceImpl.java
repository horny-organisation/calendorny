package ru.calendorny.taskservice.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.TaskProcessorException;
import ru.calendorny.taskservice.service.TaskManagerService;
import ru.calendorny.taskservice.service.TaskProcessor;

@Service
@RequiredArgsConstructor
public class BaseTaskManagerServiceImpl implements TaskManagerService {

    private final List<TaskProcessor> processors;

    private TaskProcessor getProcessor(UUID taskId) {
        return processors.stream()
            .filter(p -> p.supports(taskId))
            .findFirst()
            .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        return getProcessor(taskId).getTask(taskId);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, UUID userId, String title, String description, LocalDate dueDate,
                                   TaskStatus status, RruleDto rruleDto) {

        TaskProcessor currentProcessor = getProcessor(taskId);
        boolean shouldBeRecur = rruleDto != null;
        boolean isCurrentlyRecur = currentProcessor.supportsRecurTask(true);

        if (shouldBeRecur != isCurrentlyRecur) {

            currentProcessor.hardDeleteTask(taskId);
            TaskProcessor newProcessor = processors.stream()
                    .filter(p -> p.supportsRecurTask(shouldBeRecur))
                    .findFirst()
                    .orElseThrow(TaskProcessorException::new);

            return newProcessor.createTask(userId, title, description, dueDate, rruleDto);
        }
        return currentProcessor.updateTask(taskId, title, description, dueDate, status, rruleDto);
    }

    @Override
    public void deleteTask(UUID taskId) {
        getProcessor(taskId).deleteTask(taskId);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        return getProcessor(taskId).updateStatus(taskId, status);
    }

    @Override
    public TaskResponse createTask(UUID userId, String title, String description, LocalDate dueDate, RruleDto rruleDto) {

        TaskProcessor currentProcessor = processors.stream()
                .filter(p -> p.supportsRecurTask(rruleDto != null))
                .findFirst()
                .orElseThrow(TaskProcessorException::new);

        return currentProcessor.createTask(userId, title, description, dueDate, rruleDto);
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {

        List<TaskResponse> tasks = new ArrayList<>();
        for (TaskProcessor processor : processors) {
            tasks.addAll(processor.getTasksByDateRange(userId, fromDate, toDate));
        }
        return tasks;
    }
}
