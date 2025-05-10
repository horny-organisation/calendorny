package ru.calendorny.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Service;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.repository.SingleTaskRepository;
import ru.calendorny.taskservice.service.TaskService;
import ru.calendorny.taskservice.util.RruleDto;
import ru.calendorny.taskservice.util.RruleConverter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BaseTaskServiceImpl implements TaskService {

    private final SingleTaskRepository singleTaskRepository;

    private final RecurTaskRepository recurTaskRepository;

    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createNewTask(UUID userId, String title, String description, LocalDate dueDate, RruleDto rrule) {

        String rruleString = RruleConverter.toRruleString(rrule);

        if (rruleString == null || rruleString.isBlank() || rruleString.isEmpty()) {
            SingleTaskEntity newSingleTask = SingleTaskEntity.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.PENDING)
                .build();

            SingleTaskEntity savedSingleTask = singleTaskRepository.save(newSingleTask);
            return taskMapper.fromSingleTaskToResponse(savedSingleTask);

        } else {

            RecurTaskEntity newRecurTask = RecurTaskEntity.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .status(TaskStatus.PENDING)
                .rrule(rruleString)
                .nextDate(dueDate)
                .build();

            RecurTaskEntity savedRecurTask = recurTaskRepository.save(newRecurTask);
            return taskMapper.fromRecurTaskToResponse(savedRecurTask);
        }
    }

    @Override
    public List<TaskResponse> getTasksListByUserIdInTimeInterval(UUID userId, LocalDate fromDate, LocalDate toDate) {

        List<SingleTaskEntity> singleTaskEntities = singleTaskRepository.findAllByUserIdAndDueDateBetween(userId, fromDate, toDate);

        List<TaskResponse> taskResponses = new ArrayList<>(singleTaskEntities.stream()
                .map(taskMapper::fromSingleTaskToResponse)
                .toList());

        List<RecurTaskEntity> recurTasks = recurTaskRepository.findAllByUserId(userId);
        for (RecurTaskEntity recurTask : recurTasks) {
            taskResponses.addAll(generateOccurrences(recurTask, fromDate, toDate));
        }

        return taskResponses;
    }

    @Override
    public TaskResponse getTaskDetailsByTaskId(UUID taskId) {
        Optional<SingleTaskEntity> singleTaskOptional = singleTaskRepository.findById(taskId);
        if (singleTaskOptional.isPresent()) {
            return taskMapper.fromSingleTaskToResponse(singleTaskOptional.get());
        }
        Optional<RecurTaskEntity> recurTaskOptional = recurTaskRepository.findById(taskId);
        if (recurTaskOptional.isPresent()) {
            return taskMapper.fromRecurTaskToResponse(recurTaskOptional.get());
        }
        throw new TaskNotFoundException();
    }

    @Override
    public TaskResponse updateTaskById(UUID taskId, String title, String description, LocalDate dueDate, TaskStatus status, RruleDto rruleDto) {

        Optional<SingleTaskEntity> singleTaskOptional = singleTaskRepository.findById(taskId);
        if (singleTaskOptional.isPresent()) {
            SingleTaskEntity task = singleTaskOptional.get();
            if (rruleDto == null) {
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setStatus(status);

                SingleTaskEntity savedSingleTask = singleTaskRepository.save(task);

                return taskMapper.fromSingleTaskToResponse(savedSingleTask);
            } else {
                String rruleString = RruleConverter.toRruleString(rruleDto);
                RecurTaskEntity recurrenceTask = RecurTaskEntity.builder()
                    .userId(task.getUserId())
                    .title(title)
                    .description(description)
                    .status(TaskStatus.PENDING)
                    .rrule(rruleString)
                    .nextDate(dueDate)
                    .build();

                singleTaskRepository.delete(task);
                RecurTaskEntity savedRecurTask = recurTaskRepository.save(recurrenceTask);

                return taskMapper.fromRecurTaskToResponse(savedRecurTask);
            }
        }

        Optional<RecurTaskEntity> recurTaskOptional = recurTaskRepository.findById(taskId);

        if (recurTaskOptional.isPresent()) {
            RecurTaskEntity recurTask = recurTaskOptional.get();
            if (rruleDto != null) {
                String rruleString = RruleConverter.toRruleString(rruleDto);
                recurTask.setTitle(title);
                recurTask.setDescription(description);
                recurTask.setStatus(status);
                recurTask.setRrule(rruleString);
                recurTask.setNextDate(dueDate);

                RecurTaskEntity savedRecurTask = recurTaskRepository.save(recurTask);
                return taskMapper.fromRecurTaskToResponse(savedRecurTask);
            } else {
                SingleTaskEntity task = SingleTaskEntity.builder()
                    .userId(recurTask.getUserId())
                    .title(title)
                    .description(description)
                    .status(status)
                    .dueDate(dueDate)
                    .build();

                recurTaskRepository.delete(recurTask);
                SingleTaskEntity savedTask = singleTaskRepository.save(task);
                return taskMapper.fromSingleTaskToResponse(savedTask);
            }
        }
        throw new TaskNotFoundException();
    }

    @Override
    public void deleteTaskById(UUID taskId) {
        Optional<SingleTaskEntity> taskOptional = singleTaskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            SingleTaskEntity task = taskOptional.get();
            task.setStatus(TaskStatus.CANCELLED);
            singleTaskRepository.save(task);
        } else {
            Optional<RecurTaskEntity> recurTaskOptional = recurTaskRepository.findById(taskId);
            if (recurTaskOptional.isPresent()) {
                RecurTaskEntity recurrenceTask = recurTaskOptional.get();
                recurrenceTask.setStatus(TaskStatus.CANCELLED);
                recurTaskRepository.save(recurrenceTask);
            } else {
                throw new TaskNotFoundException();
            }
        }
    }


    @Override
    public TaskResponse updateTaskStatusByTaskId(UUID taskId, TaskStatus newStatus) {

        Optional<SingleTaskEntity> taskOptional = singleTaskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            SingleTaskEntity task = taskOptional.get();
            task.setStatus(newStatus);
            SingleTaskEntity savedTask = singleTaskRepository.save(task);
            return taskMapper.fromSingleTaskToResponse(savedTask);
        } else {
            Optional<RecurTaskEntity> recurTaskOptional = recurTaskRepository.findById(taskId);
            if (recurTaskOptional.isPresent()) {
                RecurTaskEntity recurrenceTask = recurTaskOptional.get();
                recurrenceTask.setStatus(newStatus);
                RecurTaskEntity savedTask = recurTaskRepository.save(recurrenceTask);
                return taskMapper.fromRecurTaskToResponse(savedTask);
            } else {
                throw new TaskNotFoundException();
            }
        }
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
                    result.add(taskMapper.fromRecurTaskToResponse(recurrenceTask, nextDate));
                }
            }
        } catch (InvalidRecurrenceRuleException e) {

        }
        return result;
    }
}
