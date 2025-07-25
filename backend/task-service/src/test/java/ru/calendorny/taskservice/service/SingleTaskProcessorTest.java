package ru.calendorny.taskservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.SingleTaskRepository;
import ru.calendorny.taskservice.service.impl.SingleTaskProcessor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SingleTaskProcessorTest {

    private SingleTaskRepository repository;
    private TaskMapper mapper;
    private SingleTaskProcessor singleTaskProcessor;

    private static final UUID TASK_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate NOW = LocalDate.of(2023, 1, 1);
    private static final String TITLE = "Test Task";
    private static final String DESCRIPTION = "Test Description";
    private static final TaskStatus PENDING_STATUS = TaskStatus.PENDING;
    private static final TaskStatus COMPLETED_STATUS = TaskStatus.COMPLETED;

    private static final RruleDto RRULE_DTO = RruleDto.builder()
        .frequency(TaskFrequency.WEEKLY)
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();

    private static final SingleTaskEntity SINGLE_TASK_ENTITY = SingleTaskEntity.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NOW)
        .status(PENDING_STATUS)
        .build();

    private static final SingleTaskEntity COMPLETED_SINGLE_TASK_ENTITY = SingleTaskEntity.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NOW)
        .status(COMPLETED_STATUS)
        .build();

    private static final TaskResponse TASK_RESPONSE = TaskResponse.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NOW)
        .status(PENDING_STATUS)
        .build();

    private static final TaskResponse COMPLETED_TASK_RESPONSE = TaskResponse.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NOW)
        .status(COMPLETED_STATUS)
        .build();

    private static final int TASKS_LIMIT = 100;

    @BeforeEach
    void setUp() {
        repository = mock(SingleTaskRepository.class);
        mapper = mock(TaskMapper.class);
        singleTaskProcessor = new SingleTaskProcessor(repository, mapper);
    }

    @Test
    void testSupportsWhenTaskExists() {
        when(repository.existsById(TASK_ID)).thenReturn(true);
        assertTrue(singleTaskProcessor.supports(TASK_ID));
    }

    @Test
    void testSupportsWhenTaskDoesNotExist() {
        when(repository.existsById(TASK_ID)).thenReturn(false);
        assertFalse(singleTaskProcessor.supports(TASK_ID));
    }

    @Test
    void testSupportsRecurTaskForNonRecurringTask() {
        assertTrue(singleTaskProcessor.supportsRecurTask(false));
    }

    @Test
    void testSupportsRecurTaskForRecurringTask() {
        assertFalse(singleTaskProcessor.supportsRecurTask(true));
    }

    @Test
    void testGetTaskWhenExists() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(SINGLE_TASK_ENTITY));
        when(mapper.fromSingleTaskToResponse(SINGLE_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        TaskResponse result = singleTaskProcessor.getTask(TASK_ID);
        assertEquals(TASK_RESPONSE, result);
    }

    @Test
    void testGetTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> singleTaskProcessor.getTask(TASK_ID));
    }

    @Test
    void testCreateTaskSuccess() {
        when(repository.save(any(SingleTaskEntity.class))).thenReturn(SINGLE_TASK_ENTITY);
        when(mapper.fromSingleTaskToResponse(SINGLE_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        TaskResponse result = singleTaskProcessor.createTask(USER_ID, TITLE, DESCRIPTION, NOW, null);
        assertEquals(TASK_RESPONSE, result);
        verify(repository).save(any(SingleTaskEntity.class));
    }

    @Test
    void testCreateTaskWhenRruleIsNotNull() {
        assertThrows(IllegalArgumentException.class,
            () -> singleTaskProcessor.createTask(USER_ID, TITLE, DESCRIPTION, NOW, RRULE_DTO));
    }

    @Test
    void testUpdateTaskWithExistingTask() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(SINGLE_TASK_ENTITY));
        when(repository.save(SINGLE_TASK_ENTITY)).thenReturn(COMPLETED_SINGLE_TASK_ENTITY);
        when(mapper.fromSingleTaskToResponse(COMPLETED_SINGLE_TASK_ENTITY)).thenReturn(COMPLETED_TASK_RESPONSE);

        TaskResponse result = singleTaskProcessor.updateTask(
            TASK_ID, TITLE, DESCRIPTION, NOW, COMPLETED_STATUS, null);
        assertEquals(COMPLETED_TASK_RESPONSE, result);
    }

    @Test
    void testUpdateTaskWhenRruleIsNotNull() {
        assertThrows(IllegalArgumentException.class,
            () -> singleTaskProcessor.updateTask(TASK_ID, TITLE, DESCRIPTION, NOW, PENDING_STATUS, RRULE_DTO));
    }

    @Test
    void testUpdateTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,
            () -> singleTaskProcessor.updateTask(TASK_ID, TITLE, DESCRIPTION, NOW, PENDING_STATUS, null));
    }

    @Test
    void testDeleteTaskSuccess() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(SINGLE_TASK_ENTITY));
        when(repository.save(SINGLE_TASK_ENTITY)).thenReturn(SINGLE_TASK_ENTITY);

        singleTaskProcessor.deleteTask(TASK_ID);
        assertEquals(TaskStatus.CANCELLED, SINGLE_TASK_ENTITY.getStatus());
        verify(repository).save(SINGLE_TASK_ENTITY);
    }

    @Test
    void testDeleteTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> singleTaskProcessor.deleteTask(TASK_ID));
    }

    @Test
    void testUpdateStatusSuccess() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(SINGLE_TASK_ENTITY));
        when(repository.save(SINGLE_TASK_ENTITY)).thenReturn(COMPLETED_SINGLE_TASK_ENTITY);
        when(mapper.fromSingleTaskToResponse(COMPLETED_SINGLE_TASK_ENTITY)).thenReturn(COMPLETED_TASK_RESPONSE);

        TaskResponse result = singleTaskProcessor.updateStatus(TASK_ID, COMPLETED_STATUS);
        assertEquals(COMPLETED_TASK_RESPONSE, result);
    }

    @Test
    void testHardDeleteTask() {
        singleTaskProcessor.hardDeleteTask(TASK_ID);
        verify(repository).deleteById(TASK_ID);
    }

    @Test
    void testGetTasksByDateRange() {
        List<SingleTaskEntity> tasks = List.of(SINGLE_TASK_ENTITY);
        List<TaskResponse> responses = List.of(TASK_RESPONSE);

        when(repository.findAllActiveByUserIdAndDateInterval(USER_ID, NOW, NOW.plusDays(1), TASKS_LIMIT))
            .thenReturn(tasks);
        when(mapper.fromSingleTaskToResponse(SINGLE_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        List<TaskResponse> result = singleTaskProcessor.getTasksByDateRange(USER_ID, NOW, NOW.plusDays(1));
        assertEquals(responses, result);
    }

    @Test
    void testGetPendingTasksByDate() {
        List<SingleTaskEntity> tasks = List.of(SINGLE_TASK_ENTITY);
        List<TaskResponse> responses = List.of(TASK_RESPONSE);

        when(repository.findAllPendingByDueDate(NOW)).thenReturn(tasks);
        when(mapper.fromSingleTaskToResponse(SINGLE_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        List<TaskResponse> result = singleTaskProcessor.getPendingTasksByDate(NOW);
        assertEquals(responses, result);
    }
}
