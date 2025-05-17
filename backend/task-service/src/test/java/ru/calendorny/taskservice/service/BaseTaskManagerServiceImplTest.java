package ru.calendorny.taskservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.exception.TaskProcessorException;
import ru.calendorny.taskservice.service.impl.BaseTaskManagerServiceImpl;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseTaskManagerServiceImplTest {

    @Mock
    private TaskProcessor singleTaskProcessor;

    @Mock
    private TaskProcessor recurTaskProcessor;

    @InjectMocks
    private BaseTaskManagerServiceImpl taskManagerService;

    private final UUID taskId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final String title = "Test Task";
    private final String description = "Test Description";
    private final LocalDate date = LocalDate.now();
    private final RruleDto rruleDto = new RruleDto(RruleDto.Frequency.WEEKLY, DayOfWeek.MONDAY, null);
    private final TaskResponse taskResponse = TaskResponse.builder()
        .id(taskId)
        .userId(userId)
        .title(title)
        .description(description)
        .dueDate(date)
        .status(TaskStatus.PENDING)
        .build();

    @BeforeEach
    void setUp() {
        taskManagerService = new BaseTaskManagerServiceImpl(List.of(singleTaskProcessor, recurTaskProcessor));
    }

    @Test
    void getTask_shouldReturnTaskWhenFound() {
        when(recurTaskProcessor.supports(taskId)).thenReturn(true);
        when(recurTaskProcessor.getTask(taskId)).thenReturn(taskResponse);

        TaskResponse result = taskManagerService.getTask(taskId);

        assertEquals(taskResponse, result);
        verify(recurTaskProcessor).supports(taskId);
        verify(recurTaskProcessor).getTask(taskId);
    }

    @Test
    void getTask_shouldThrowExceptionWhenTaskNotFound() {
        when(singleTaskProcessor.supports(taskId)).thenReturn(false);
        when(recurTaskProcessor.supports(taskId)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskManagerService.getTask(taskId));
    }

    @Test
    void createTask_shouldUseSingleTaskProcessorWhenNoRecurrence() {
        when(singleTaskProcessor.supportsRecurTask(false)).thenReturn(true);
        when(singleTaskProcessor.createTask(userId, title, description, date, null))
            .thenReturn(taskResponse);

        TaskResponse result = taskManagerService.createTask(userId, title, description, date, null);

        assertEquals(taskResponse, result);
        verify(singleTaskProcessor).supportsRecurTask(false);
        verify(singleTaskProcessor).createTask(userId, title, description, date, null);
    }

    @Test
    void createTask_shouldUseRecurTaskProcessorWhenRecurrenceExists() {
        when(recurTaskProcessor.supportsRecurTask(true)).thenReturn(true);
        when(recurTaskProcessor.createTask(userId, title, description, date, rruleDto))
            .thenReturn(taskResponse);

        TaskResponse result = taskManagerService.createTask(userId, title, description, date, rruleDto);

        assertEquals(taskResponse, result);
        verify(recurTaskProcessor).supportsRecurTask(true);
        verify(recurTaskProcessor).createTask(userId, title, description, date, rruleDto);
    }

    @Test
    void createTask_shouldThrowExceptionWhenNoSuitableProcessorFound() {
        when(singleTaskProcessor.supportsRecurTask(true)).thenReturn(false);
        when(recurTaskProcessor.supportsRecurTask(true)).thenReturn(false);

        assertThrows(TaskProcessorException.class,
            () -> taskManagerService.createTask(userId, title, description, date, rruleDto));
    }

    @Test
    void updateTask_shouldUpdateWithSameProcessorWhenRecurrenceNotChanged() {
        when(recurTaskProcessor.supports(taskId)).thenReturn(true);
        when(recurTaskProcessor.supportsRecurTask(true)).thenReturn(true);
        when(recurTaskProcessor.updateTask(taskId, title, description, date, TaskStatus.PENDING, rruleDto))
            .thenReturn(taskResponse);

        TaskResponse result = taskManagerService.updateTask(
            taskId, userId, title, description, date, TaskStatus.PENDING, rruleDto);

        assertEquals(taskResponse, result);
        verify(recurTaskProcessor).supports(taskId);
        verify(recurTaskProcessor).updateTask(taskId, title, description, date, TaskStatus.PENDING, rruleDto);
    }

    @Test
    void updateTask_shouldSwitchProcessorWhenRecurrenceChanged() {
        // Current processor is single task processor
        when(singleTaskProcessor.supports(taskId)).thenReturn(true);
        when(recurTaskProcessor.supportsRecurTask(true)).thenReturn(true);
        when(recurTaskProcessor.createTask(userId, title, description, date, rruleDto))
            .thenReturn(taskResponse);

        TaskResponse result = taskManagerService.updateTask(
            taskId, userId, title, description, date, TaskStatus.PENDING, rruleDto);

        assertEquals(taskResponse, result);
        verify(singleTaskProcessor).hardDeleteTask(taskId);
        verify(recurTaskProcessor).createTask(userId, title, description, date, rruleDto);
    }

    @Test
    void deleteTask_shouldCallProcessorDeleteMethod() {
        when(singleTaskProcessor.supports(taskId)).thenReturn(true);
        doNothing().when(singleTaskProcessor).deleteTask(taskId);

        taskManagerService.deleteTask(taskId);

        verify(singleTaskProcessor).supports(taskId);
        verify(singleTaskProcessor).deleteTask(taskId);
    }

    @Test
    void updateStatus_shouldCallProcessorUpdateStatusMethod() {
        when(recurTaskProcessor.supports(taskId)).thenReturn(true);
        when(recurTaskProcessor.updateStatus(taskId, TaskStatus.COMPLETED))
            .thenReturn(taskResponse);

        TaskResponse result = taskManagerService.updateStatus(taskId, TaskStatus.COMPLETED);

        assertEquals(taskResponse, result);
        verify(recurTaskProcessor).supports(taskId);
        verify(recurTaskProcessor).updateStatus(taskId, TaskStatus.COMPLETED);
    }

    @Test
    void getTasksByDateRange_shouldCombineResultsFromAllProcessors() {
        when(singleTaskProcessor.getTasksByDateRange(userId, date, date.plusDays(1)))
            .thenReturn(List.of(taskResponse));
        when(recurTaskProcessor.getTasksByDateRange(userId, date, date.plusDays(1)))
            .thenReturn(List.of(taskResponse));

        List<TaskResponse> results = taskManagerService.getTasksByDateRange(userId, date, date.plusDays(1));

        assertEquals(2, results.size());
        verify(singleTaskProcessor).getTasksByDateRange(userId, date, date.plusDays(1));
        verify(recurTaskProcessor).getTasksByDateRange(userId, date, date.plusDays(1));
    }
}
