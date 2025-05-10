package ru.calendorny.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.SingleTaskRepository;
import ru.calendorny.taskservice.service.TaskProcessor;
import ru.calendorny.taskservice.util.RruleDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SingleTaskProcessor implements TaskProcessor {

    private final SingleTaskRepository repository;

    private final TaskMapper mapper;

    @Override
    public boolean supports(UUID taskId) {
        return repository.existsById(taskId);
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        return repository.findById(taskId).map(mapper::fromSingleTaskToResponse).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto) {
        if (rruleDto != null) {
            throw new IllegalArgumentException("Can't update SingleTask with recurrence rule");
        }
        SingleTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setTitle(title);
        task.setDescription(desc);
        task.setDueDate(date);
        task.setStatus(status);

        SingleTaskEntity savedTask = repository.save(task);
        return mapper.fromSingleTaskToResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {
        SingleTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setStatus(TaskStatus.CANCELLED);
        SingleTaskEntity savedTask = repository.save(task);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        SingleTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setStatus(status);
        SingleTaskEntity savedTask = repository.save(task);
        return mapper.fromSingleTaskToResponse(task);
    }

    @Override
    public void hardDeleteTask(UUID taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public boolean supportsRecurringTask(boolean recurring) {
        return !recurring;
    }

    @Override
    public TaskResponse createTask(UUID userId, String title, String desc, LocalDate date, RruleDto rruleDto) {
        if (rruleDto != null) {
            throw new IllegalArgumentException("SingleTask cannot have recurrence rule");
        }
        SingleTaskEntity newTask = SingleTaskEntity.builder()
            .title(title)
            .description(desc)
            .userId(userId)
            .dueDate(date)
            .status(TaskStatus.PENDING)
            .build();

        SingleTaskEntity savedTask = repository.save(newTask);
        return mapper.fromSingleTaskToResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getTasksByUserIdAndDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {
        List<SingleTaskEntity> tasks = repository.findAllByUserIdAndDueDateBetween(userId, fromDate, toDate);
        return tasks.stream()
            .map(mapper::fromSingleTaskToResponse)
            .toList();
    }
}
