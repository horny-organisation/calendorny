package ru.calendorny.taskservice.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.service.TaskProcessor;
import ru.calendorny.taskservice.util.RruleCalculator;
import ru.calendorny.taskservice.util.RruleConverter;
import ru.calendorny.taskservice.util.SingleTaskHelper;

@Service
public class RecurTaskProcessor implements TaskProcessor {

    private final RecurTaskRepository repository;

    private final SingleTaskHelper singleTaskHelper;

    private final TaskMapper mapper;

    public RecurTaskProcessor(RecurTaskRepository repository, SingleTaskHelper singleTaskHelper, TaskMapper mapper) {
        this.repository = repository;
        this.singleTaskHelper = singleTaskHelper;
        this.mapper = mapper;
    }

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
        return repository.findById(taskId).map(mapper::fromRecurTaskToResponse).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskResponse createTask(UUID userId, String title, String desc, LocalDate date, RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new IllegalStateException("Can't update RecurTask without recurrence rule");
        }
        RecurTaskEntity newTask = RecurTaskEntity.builder()
                .title(title)
                .description(desc)
                .userId(userId)
                .nextDate(date)
                .rrule(RruleConverter.toRruleString(rruleDto))
                .status(TaskStatus.PENDING)
                .build();

        RecurTaskEntity savedTask = repository.save(newTask);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(
            UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new IllegalStateException("Can't update RecurTask without recurrence rule");
        }
        String rruleString = RruleConverter.toRruleString(rruleDto);
        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setRrule(rruleString);
        task.setNextDate(date);

        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {

        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setStatus(TaskStatus.CANCELLED);
        repository.save(task);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        LocalDate prevDate = task.getNextDate();
        if (status.equals(TaskStatus.COMPLETED)) {
            singleTaskHelper.createCompletedSingleTask(
                    task.getTitle(), task.getDescription(), task.getUserId(), prevDate);
        }
        task.setNextDate(RruleCalculator.findNextDate(task.getRrule(), prevDate));
        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void hardDeleteTask(UUID taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public List<TaskResponse> getTasksByDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {

        List<RecurTaskEntity> tasks = repository.findAllActiveByUserIdAndDateInterval(userId, fromDate, toDate);
        return tasks.stream().map(mapper::fromRecurTaskToResponse).toList();
    }

    @Override
    public List<TaskResponse> getPendingTasksByDate(LocalDate date) {
        List<RecurTaskEntity> tasks = repository.findAllPendingByNextDate(date);
        return tasks.stream().map(mapper::fromRecurTaskToResponse).toList();
    }
}
