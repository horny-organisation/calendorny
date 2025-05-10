package ru.calendorny.taskservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.TaskProcessorException;
import ru.calendorny.taskservice.service.impl.RecurTaskProcessor;
import ru.calendorny.taskservice.util.RruleDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskManagerService {

    private final List<TaskProcessor> processors;

    private TaskProcessor getProcessor(UUID taskId) {
        return processors.stream()
            .filter(p -> p.supports(taskId))
            .findFirst()
            .orElseThrow(TaskNotFoundException::new);
    }

    public TaskResponse getTask(UUID taskId) {
        return getProcessor(taskId).getTask(taskId);
    }

    public TaskResponse updateTask(UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto) {
        TaskProcessor currentProcessor = getProcessor(taskId);

        boolean shouldBeRecur = rruleDto != null;
        boolean isCurrentlyRecur = currentProcessor instanceof RecurTaskProcessor;

        if (shouldBeRecur != isCurrentlyRecur) {
            currentProcessor.hardDeleteTask(taskId);

            TaskProcessor newProcessor = processors.stream()
                .filter(p -> p.supportsRecurringTask(shouldBeRecur))
                .findFirst()
                .orElseThrow(TaskProcessorException::new);

            // return newProcessor.createTask
        }

        return currentProcessor.updateTask(taskId, title, desc, date, status, rruleDto);
    }

    public void deleteTask(UUID taskId) {
        getProcessor(taskId).deleteTask(taskId);
    }

    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        return getProcessor(taskId).updateStatus(taskId, status);
    }

    public TaskResponse createTask(UUID userId, String title, String desc, LocalDate date, RruleDto rruleDto) {
        TaskProcessor currentProcessor = processors.stream()
            .filter(p -> p.supportsRecurringTask(rruleDto != null))
            .findFirst()
            .orElseThrow(TaskProcessorException::new);

        return currentProcessor.createTask(userId, title, desc, date, rruleDto);
    }

    public List<TaskResponse> getTasksListByUserIdInTimeInterval(UUID userId, LocalDate from, LocalDate to) {
        List<TaskResponse> tasks = new ArrayList<>();
        for (TaskProcessor processor : processors) {
            tasks.addAll(processor.getTasksByUserIdAndDateRange(userId, from, to));
        }
        return tasks;
    }
}
