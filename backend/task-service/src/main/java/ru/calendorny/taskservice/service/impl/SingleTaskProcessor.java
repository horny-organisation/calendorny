package ru.calendorny.taskservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.SingleTaskRepository;
import ru.calendorny.taskservice.service.TaskProcessor;

@Service
@RequiredArgsConstructor
public class SingleTaskProcessor implements TaskProcessor {

    private final SingleTaskRepository repository;

    private final TaskMapper mapper;

    private static final int tasksLimit = 100;

    @Override
    public boolean supports(UUID taskId) {
        return repository.existsById(taskId);
    }

    @Override
    public boolean supportsRecurTask(boolean isRecur) {
        return !isRecur;
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        return repository.findById(taskId)
            .map(mapper::fromSingleTaskToResponse)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    public TaskResponse createTask(UUID userId, String title, String description, LocalDate dueDate, RruleDto rruleDto) {
        if (rruleDto != null) {
            throw new IllegalArgumentException("SingleTask cannot have recurrence rule");
        }
        SingleTaskEntity newTask = SingleTaskEntity.builder()
            .title(title)
            .description(description)
            .userId(userId)
            .dueDate(dueDate)
            .status(TaskStatus.PENDING)
            .build();

        SingleTaskEntity savedTask = repository.save(newTask);

        return mapper.fromSingleTaskToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, String title, String description, LocalDate dueDate,
                                   TaskStatus status, RruleDto rruleDto) {
        if (rruleDto != null) {
            throw new IllegalArgumentException("Can't update SingleTask with recurrence rule");
        }
        SingleTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(status);

        SingleTaskEntity savedTask = repository.save(task);

        return mapper.fromSingleTaskToResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {
        SingleTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(TaskStatus.CANCELLED);
        repository.save(task);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        SingleTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(status);
        SingleTaskEntity savedTask = repository.save(task);
        return mapper.fromSingleTaskToResponse(savedTask);
    }

    @Override
    public void hardDeleteTask(UUID taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {
        List<SingleTaskEntity> tasks = repository.findAllActiveByUserIdAndDateInterval(userId, fromDate, toDate, tasksLimit);
        return tasks.stream()
            .map(mapper::fromSingleTaskToResponse)
            .toList();
    }

    @Override
    public List<TaskResponse> getPendingTasksByDate(LocalDate date) {
        List<SingleTaskEntity> tasks = repository.findAllPendingByDueDate(date);
        return tasks.stream()
            .map(mapper::fromSingleTaskToResponse)
            .toList();
    }
}
