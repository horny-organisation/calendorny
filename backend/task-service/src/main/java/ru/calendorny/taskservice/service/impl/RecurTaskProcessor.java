package ru.calendorny.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.RruleParsingException;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.service.TaskProcessor;
import ru.calendorny.taskservice.util.RruleCalculator;
import ru.calendorny.taskservice.util.RruleConverter;
import ru.calendorny.taskservice.util.RruleDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecurTaskProcessor implements TaskProcessor {

    private final RecurTaskRepository repository;

    private final TaskMapper mapper;

    @Override
    public boolean supports(UUID taskId) {
        return repository.existsById(taskId);
    }

    @Override
    public TaskResponse getTask(UUID taskId) {
        return repository.findById(taskId).map(mapper::fromRecurTaskToResponse).orElseThrow(TaskNotFoundException::new);
    }

    @Override
    public TaskResponse updateTask(UUID taskId, String title, String desc, LocalDate date, TaskStatus status, RruleDto rruleDto) {
        if (rruleDto == null) {
            throw new IllegalStateException("Can't update RecurTask without recurrence rule");
        }
        String rruleString = RruleConverter.toRruleString(rruleDto);
        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setRrule(rruleString);
        task.setNextDate(RruleCalculator.findNextDate(rruleString, date));

        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void deleteTask(UUID taskId) {
        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setStatus(TaskStatus.CANCELLED);
        RecurTaskEntity savedTask = repository.save(task);
    }

    @Override
    public TaskResponse updateStatus(UUID taskId, TaskStatus status) {
        RecurTaskEntity task = repository.findById(taskId).orElseThrow(TaskNotFoundException::new);
        task.setStatus(status);
        RecurTaskEntity savedTask = repository.save(task);
        return mapper.fromRecurTaskToResponse(savedTask);
    }

    @Override
    public void hardDeleteTask(UUID taskId) {
        repository.deleteById(taskId);
    }

    @Override
    public boolean supportsRecurringTask(boolean recurring) {
        return recurring;
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
    public List<TaskResponse> getTasksByUserIdAndDateRange(UUID userId, LocalDate fromDate, LocalDate toDate) {
        List<TaskResponse> tasks = new ArrayList<>();
        List<RecurTaskEntity> recurTasks = repository.findAllByUserId(userId);
        for (RecurTaskEntity recurTask : recurTasks) {
            tasks.addAll(generateOccurrences(recurTask, fromDate, toDate));
        }
        return tasks;
    }

    private List<TaskResponse> generateOccurrences(RecurTaskEntity recurrenceTask, LocalDate from, LocalDate to) {
        List<TaskResponse> result = new ArrayList<>();

        DateTime start = new DateTime(from.getYear(), from.getMonthValue() - 1, from.getDayOfMonth());
        try {
            RecurrenceRule rule = new RecurrenceRule(recurrenceTask.getRrule());

            RecurrenceRuleIterator it = rule.iterator(start);

            while (it.hasNext()) {
                DateTime next = it.next();

                LocalDate nextDate = LocalDate.of(next.getYear(), next.getMonth() + 1, next.getDayOfMonth());

                if (nextDate.isAfter(to)) {
                    break;
                }

                if (!nextDate.isBefore(from)) {
                    result.add(mapper.fromRecurTaskToResponse(recurrenceTask, nextDate));
                }
            }
        } catch (InvalidRecurrenceRuleException e) {

        }
        return result;
    }
}
