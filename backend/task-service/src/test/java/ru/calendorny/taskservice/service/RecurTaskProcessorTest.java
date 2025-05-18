package ru.calendorny.taskservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.exception.TaskNotFoundException;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.service.impl.RecurTaskProcessor;
import ru.calendorny.taskservice.util.rrule.RruleCalculator;
import ru.calendorny.taskservice.util.rrule.RruleConverter;
import ru.calendorny.taskservice.util.SingleTaskHelper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecurTaskProcessorTest {

    private static final UUID TASK_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final LocalDate NOW = LocalDate.of(2023, 1, 1);
    private static final LocalDate NEXT_DATE = NOW.plusDays(7);
    private static final String TITLE = "Test Task";
    private static final String DESCRIPTION = "Test Description";
    private static final RruleDto RRULE_DTO = RruleDto.builder()
        .frequency(RruleDto.Frequency.WEEKLY)
        .dayOfWeek(DayOfWeek.MONDAY)
        .build();
    private static final String RRULE_STRING = "FREQ=WEEKLY;BYDAY=MO";
    private static final TaskStatus PENDING_STATUS = TaskStatus.PENDING;
    private static final TaskStatus COMPLETED_STATUS = TaskStatus.COMPLETED;

    private static final RecurTaskEntity RECUR_TASK_ENTITY = RecurTaskEntity.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .nextDate(NOW)
        .rrule(RRULE_STRING)
        .status(PENDING_STATUS)
        .build();

    private static final RecurTaskEntity COMPLETED_RECUR_TASK_ENTITY = RecurTaskEntity.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .nextDate(NOW)
        .rrule(RRULE_STRING)
        .status(COMPLETED_STATUS)
        .build();

    private static final TaskResponse TASK_RESPONSE = TaskResponse.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NOW)
        .status(PENDING_STATUS)
        .recurrenceRule(RRULE_DTO)
        .build();

    private static final TaskResponse COMPLETED_TASK_RESPONSE = TaskResponse.builder()
        .id(TASK_ID)
        .userId(USER_ID)
        .title(TITLE)
        .description(DESCRIPTION)
        .dueDate(NEXT_DATE)
        .status(COMPLETED_STATUS)
        .recurrenceRule(RRULE_DTO)
        .build();

    @MockitoBean
    private RecurTaskRepository repository;
    @MockitoBean
    private SingleTaskHelper singleTaskHelper;
    @MockitoBean
    private TaskMapper mapper;
    @MockitoBean
    private RruleConverter rruleConverter;
    @MockitoBean
    private RruleCalculator rruleCalculator;

    @Autowired
    private RecurTaskProcessor recurTaskProcessor;

    @Test
    void testSupportsWhenTaskExists() {
        when(repository.existsById(TASK_ID)).thenReturn(true);
        assertTrue(recurTaskProcessor.supports(TASK_ID));
    }

    @Test
    void testSupportsWhenTaskDoesNotExist() {
        when(repository.existsById(TASK_ID)).thenReturn(false);
        assertFalse(recurTaskProcessor.supports(TASK_ID));
    }

    @Test
    void testSupportsRecurTaskForRecurringTask() {
        assertTrue(recurTaskProcessor.supportsRecurTask(true));
    }

    @Test
    void testSupportsRecurTaskForNonRecurringTask() {
        assertFalse(recurTaskProcessor.supportsRecurTask(false));
    }

    @Test
    void testGetTaskWhenExists() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(RECUR_TASK_ENTITY));
        when(mapper.fromRecurTaskToResponse(RECUR_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        TaskResponse result = recurTaskProcessor.getTask(TASK_ID);
        assertEquals(TASK_RESPONSE, result);
    }

    @Test
    void testGetTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> recurTaskProcessor.getTask(TASK_ID));
    }

    @Test
    void testCreateTaskSuccess() {
        when(rruleConverter.toRruleString(RRULE_DTO)).thenReturn(RRULE_STRING);
        when(repository.save(any(RecurTaskEntity.class))).thenReturn(RECUR_TASK_ENTITY);
        when(mapper.fromRecurTaskToResponse(RECUR_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        TaskResponse result = recurTaskProcessor.createTask(USER_ID, TITLE, DESCRIPTION, NOW, RRULE_DTO);
        assertEquals(TASK_RESPONSE, result);
        verify(repository).save(any(RecurTaskEntity.class));
    }

    @Test
    void testCreateTaskWhenRruleIsNull() {
        assertThrows(IllegalStateException.class,
            () -> recurTaskProcessor.createTask(USER_ID, TITLE, DESCRIPTION, NOW, null));
    }

    @Test
    void testUpdateTaskWithExistingTask() {
        when(rruleConverter.toRruleString(RRULE_DTO)).thenReturn(RRULE_STRING);
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(RECUR_TASK_ENTITY));
        when(repository.save(RECUR_TASK_ENTITY)).thenReturn(COMPLETED_RECUR_TASK_ENTITY);
        when(mapper.fromRecurTaskToResponse(COMPLETED_RECUR_TASK_ENTITY)).thenReturn(COMPLETED_TASK_RESPONSE);

        TaskResponse result = recurTaskProcessor.updateTask(
            TASK_ID, TITLE, DESCRIPTION, NOW, COMPLETED_STATUS, RRULE_DTO);
        assertEquals(COMPLETED_TASK_RESPONSE, result);

        assertEquals(TITLE, RECUR_TASK_ENTITY.getTitle());
        assertEquals(DESCRIPTION, RECUR_TASK_ENTITY.getDescription());
        assertEquals(COMPLETED_STATUS, RECUR_TASK_ENTITY.getStatus());
        assertEquals(NOW, RECUR_TASK_ENTITY.getNextDate());
        assertEquals(RRULE_STRING, RECUR_TASK_ENTITY.getRrule());
    }

    @Test
    void testUpdateTaskWhenRruleIsNull() {
        assertThrows(IllegalStateException.class,
            () -> recurTaskProcessor.updateTask(TASK_ID, TITLE, DESCRIPTION, NOW, PENDING_STATUS, null));
    }

    @Test
    void testUpdateTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class,
            () -> recurTaskProcessor.updateTask(TASK_ID, TITLE, DESCRIPTION, NOW, PENDING_STATUS, RRULE_DTO));
    }

    @Test
    void testDeleteTaskSuccess() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(RECUR_TASK_ENTITY));
        when(repository.save(RECUR_TASK_ENTITY)).thenReturn(RECUR_TASK_ENTITY);

        recurTaskProcessor.deleteTask(TASK_ID);
        assertEquals(TaskStatus.CANCELLED, RECUR_TASK_ENTITY.getStatus());
        verify(repository).save(RECUR_TASK_ENTITY);
    }

    @Test
    void testDeleteTaskWhenTaskNotFound() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> recurTaskProcessor.deleteTask(TASK_ID));
    }

    @Test
    void testUpdateStatusSuccess() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(RECUR_TASK_ENTITY));
        when(rruleCalculator.findNextDate(RRULE_STRING, NOW)).thenReturn(NEXT_DATE);
        when(repository.save(RECUR_TASK_ENTITY)).thenReturn(RECUR_TASK_ENTITY);
        when(mapper.fromRecurTaskToResponse(RECUR_TASK_ENTITY)).thenReturn(COMPLETED_TASK_RESPONSE);

        TaskResponse result = recurTaskProcessor.updateStatus(TASK_ID, COMPLETED_STATUS);
        assertEquals(COMPLETED_TASK_RESPONSE, result);
        assertEquals(NEXT_DATE, RECUR_TASK_ENTITY.getNextDate());
        verify(singleTaskHelper).createCompletedSingleTask(TITLE, DESCRIPTION, USER_ID, NOW);
    }

    @Test
    void testHardDeleteTask() {
        recurTaskProcessor.hardDeleteTask(TASK_ID);
        verify(repository).deleteById(TASK_ID);
    }

    @Test
    void testGetTasksByDateRange() {
        List<RecurTaskEntity> tasks = List.of(RECUR_TASK_ENTITY);
        List<TaskResponse> responses = List.of(TASK_RESPONSE);

        when(repository.findAllActiveByUserIdAndDateInterval(USER_ID, NOW, NEXT_DATE))
            .thenReturn(tasks);
        when(mapper.fromRecurTaskToResponse(RECUR_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        List<TaskResponse> result = recurTaskProcessor.getTasksByDateRange(USER_ID, NOW, NEXT_DATE);
        assertEquals(responses, result);
    }

    @Test
    void testGetPendingTasksByDate() {
        List<RecurTaskEntity> tasks = List.of(RECUR_TASK_ENTITY);
        List<TaskResponse> responses = List.of(TASK_RESPONSE);

        when(repository.findAllPendingByNextDate(NOW)).thenReturn(tasks);
        when(mapper.fromRecurTaskToResponse(RECUR_TASK_ENTITY)).thenReturn(TASK_RESPONSE);

        List<TaskResponse> result = recurTaskProcessor.getPendingTasksByDate(NOW);
        assertEquals(responses, result);
    }
}
