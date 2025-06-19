package ru.calendorny.taskservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.service.TaskProcessor;
import ru.calendorny.taskservice.util.rrule.RruleCalculator;
import ru.calendorny.taskservice.util.rrule.RruleConverter;
import ru.calendorny.taskservice.util.SingleTaskHelper;

@Service
@RequiredArgsConstructor
public class RecurTaskProcessor implements TaskProcessor {

    private final RecurTaskRepository repository;

    private final SingleTaskHelper singleTaskHelper;

    private final TaskMapper mapper;

    private final RruleConverter rruleConverter;

    private final RruleCalculator rruleCalculator;

    private static final int tasksLimit = 150;

    @Override
    public boolean supports(UUID taskId) {
        return repository.existsById(taskId);
    }

    @Override
    public boolean supportsRecurTask(boolean isRecur) {
        return isRecur;
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        return repository.findById(taskId)
            .map(mapper::fromRecurTaskToResponse)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    @Override
    public TaskResponse createTask(UUID userId, String title, String description, LocalDate dueDate, RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new IllegalStateException("Can't update RecurTask without recurrence rule");
        }
        RecurTaskEntity newTask = RecurTaskEntity.builder()
            .userId(userId)
            .title(title)
            .description(description)
            .nextDate(dueDate)
            .rrule(rruleConverter.toRruleString(rruleDto))
            .status(TaskStatus.PENDING)
            .build();

        RecurTaskEntity savedTask = repository.save(newTask);

        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, String title, String description, LocalDate dueDate,
                                   TaskStatus status, RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new IllegalStateException("Can't update RecurTask without recurrence rule");
        }
        String rruleString = rruleConverter.toRruleString(rruleDto);
        RecurTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setRrule(rruleString);
        task.setNextDate(dueDate);
        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {
        RecurTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(TaskStatus.CANCELLED);
        repository.save(task);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        RecurTaskEntity task = repository.findById(taskId)
            .orElseThrow(() -> new TaskNotFoundException(taskId));

        LocalDate prevDate = task.getNextDate();
        if (status == TaskStatus.COMPLETED) {
            singleTaskHelper.createCompletedSingleTask(
                task.getTitle(), task.getDescription(), task.getUserId(), prevDate);
            task.setNextDate(rruleCalculator.findNextDate(task.getRrule(), prevDate));
        } else if (status == TaskStatus.CANCELLED) {
            task.setStatus(TaskStatus.CANCELLED);
        }
        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void hardDeleteTask(UUID taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {
        List<RecurTaskEntity> tasks = repository.findAllActiveByUserIdAndDateInterval(userId, fromDate, toDate, tasksLimit);
        return tasks.stream()
            .map(mapper::fromRecurTaskToResponse)
            .toList();
    }

    @Override
    public List<TaskResponse> getPendingTasksByDate(LocalDate date) {
        List<RecurTaskEntity> tasks = repository.findAllPendingByNextDate(date);
        return tasks.stream()
            .map(mapper::fromRecurTaskToResponse)
            .toList();
    }
}
