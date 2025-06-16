package ru.calendorny.taskservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.dto.RruleDto;
import ru.calendorny.taskservice.dto.request.CreateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskRequest;
import ru.calendorny.taskservice.dto.request.UpdateTaskStatusRequest;
import ru.calendorny.taskservice.dto.response.ApiErrorResponse;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.dto.response.ValidationErrorResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskFrequency;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.repository.RecurTaskRepository;
import ru.calendorny.taskservice.repository.SingleTaskRepository;
import ru.calendorny.taskservice.security.JwtService;
import ru.calendorny.taskservice.util.rrule.RruleCalculator;
import ru.calendorny.taskservice.util.rrule.RruleConverter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@Import({TestContainersConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private SingleTaskRepository singleTaskRepository;

    @Autowired
    private RecurTaskRepository recurTaskRepository;

    @Autowired
    private RruleConverter rruleConverter;

    @Autowired
    private RruleCalculator rruleCalculator;

    private String accessToken;

    private HttpHeaders httpHeaders;

    private static UUID singleTaskId;

    private static UUID weeklyRecurTaskId;

    private static UUID monthlyRecurTaskId;

    private static String taskIdBaseUrl = "/api/v1/tasks/%s";

    private static String taskStatusBaseUrl = "/api/v1/tasks/%s/status";

    private static final String TASK_API_BASE_URL = "/api/v1/tasks";

    private static final UUID USER_ID = UUID.randomUUID();

    private static final String USER_EMAIL = "test@mail.ru";

    private static final CreateTaskRequest createSingleTaskRequest = new CreateTaskRequest(
        "New single task",
        "new single task description",
        LocalDate.now(),
        null
        );

    private static final CreateTaskRequest createWeeklyRecurTaskRequest = new CreateTaskRequest(
        "New weekly recur task",
        "new weekly task description",
        LocalDate.now(),
        RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(DayOfWeek.MONDAY)
            .build()
    );

    private static final CreateTaskRequest createMonthlyRecurTaskRequest = new CreateTaskRequest(
        "New monthly recur task",
        null,
        LocalDate.now().plusDays(10),
        RruleDto.builder()
            .frequency(TaskFrequency.MONTHLY)
            .dayOfMonth(15)
            .build()
    );

    private static final UpdateTaskRequest updateSingleTaskRequest = new UpdateTaskRequest(
        "Updated single task",
        "Updated single task description",
        LocalDate.now(),
        TaskStatus.PENDING,
        null
    );

    private static final UpdateTaskRequest updateRecurTaskRequest = new UpdateTaskRequest(
        "Updated recur task",
        "Updated recur task description",
        LocalDate.now(),
        TaskStatus.PENDING,
        RruleDto.builder()
            .frequency(TaskFrequency.WEEKLY)
            .dayOfWeek(DayOfWeek.THURSDAY)
            .build()
    );

    private static final UpdateTaskStatusRequest updateCompleteTaskStatusRequest = new UpdateTaskStatusRequest(
        TaskStatus.COMPLETED);

    private static final CreateTaskRequest invalidCreateSingleTaskRequest = new CreateTaskRequest(
        null,
        "Desc",
        LocalDate.now().minusDays(3),
        null
    );


    @BeforeEach
    void beforeEach() {
        accessToken = jwtService.generateAccessToken(
            USER_ID.toString(),
            Map.of(
                "email", USER_EMAIL,
                "id", USER_ID)
        );

        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @Order(1)
    void testCreateTaskWithSingleTaskRequestSuccess() {

        HttpEntity<CreateTaskRequest> httpEntity = new HttpEntity<>(createSingleTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        singleTaskId = response.getBody().id();
        assertNotNull(singleTaskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(createSingleTaskRequest.title(), response.getBody().title());
        assertEquals(createSingleTaskRequest.description(), response.getBody().description());
        assertEquals(createSingleTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(createSingleTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(TaskStatus.PENDING, response.getBody().status());

        assertTrue(singleTaskRepository.findById(singleTaskId).isPresent());
        assertFalse(recurTaskRepository.findById(singleTaskId).isPresent());

        SingleTaskEntity savedTask = singleTaskRepository.findById(singleTaskId).get();
        assertEquals(USER_ID, savedTask.getUserId());
        assertEquals(createSingleTaskRequest.title(), savedTask.getTitle());
        assertEquals(createSingleTaskRequest.description(), savedTask.getDescription());
        assertEquals(createSingleTaskRequest.dueDate(), savedTask.getDueDate());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    @Test
    @Order(2)
    void testCreateTaskWithWeeklyTaskRequestSuccess() {
        HttpEntity<CreateTaskRequest> httpEntity = new HttpEntity<>(createWeeklyRecurTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        weeklyRecurTaskId = response.getBody().id();
        assertNotNull(weeklyRecurTaskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(createWeeklyRecurTaskRequest.title(), response.getBody().title());
        assertEquals(createWeeklyRecurTaskRequest.description(), response.getBody().description());
        assertEquals(createWeeklyRecurTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(createWeeklyRecurTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(TaskStatus.PENDING, response.getBody().status());

        assertTrue(recurTaskRepository.findById(weeklyRecurTaskId).isPresent());
        assertFalse(singleTaskRepository.findById(weeklyRecurTaskId).isPresent());

        RecurTaskEntity savedTask = recurTaskRepository.findById(weeklyRecurTaskId).get();
        assertEquals(USER_ID, savedTask.getUserId());
        assertEquals(createWeeklyRecurTaskRequest.title(), savedTask.getTitle());
        assertEquals(createWeeklyRecurTaskRequest.description(), savedTask.getDescription());
        assertEquals(createWeeklyRecurTaskRequest.dueDate(), savedTask.getNextDate());
        assertEquals(createWeeklyRecurTaskRequest.rrule(), rruleConverter.toRruleDto(savedTask.getRrule()));
        assertEquals(rruleConverter.toRruleString(createWeeklyRecurTaskRequest.rrule()), savedTask.getRrule());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    @Test
    @Order(3)
    void testCreateTaskWithMonthlyTaskRequestSuccess() {
        HttpEntity<CreateTaskRequest> httpEntity = new HttpEntity<>(createMonthlyRecurTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        monthlyRecurTaskId = response.getBody().id();
        assertNotNull(monthlyRecurTaskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(createMonthlyRecurTaskRequest.title(), response.getBody().title());
        assertEquals(createMonthlyRecurTaskRequest.description(), response.getBody().description());
        assertEquals(createMonthlyRecurTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(createMonthlyRecurTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(TaskStatus.PENDING, response.getBody().status());

        assertTrue(recurTaskRepository.findById(monthlyRecurTaskId).isPresent());
        assertFalse(singleTaskRepository.findById(monthlyRecurTaskId).isPresent());

        RecurTaskEntity savedTask = recurTaskRepository.findById(monthlyRecurTaskId).get();
        assertEquals(USER_ID, savedTask.getUserId());
        assertEquals(createMonthlyRecurTaskRequest.title(), savedTask.getTitle());
        assertEquals(createMonthlyRecurTaskRequest.description(), savedTask.getDescription());
        assertEquals(createMonthlyRecurTaskRequest.dueDate(), savedTask.getNextDate());
        assertEquals(createMonthlyRecurTaskRequest.rrule(), rruleConverter.toRruleDto(savedTask.getRrule()));
        assertEquals(rruleConverter.toRruleString(createMonthlyRecurTaskRequest.rrule()), savedTask.getRrule());
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
    }

    @Test
    @Order(4)
    void testGetTasksByDateRangeSuccess() {
        String from = LocalDate.now().toString();
        String to = LocalDate.now().plusDays(2).toString();

        ResponseEntity<TaskResponse[]> response = restTemplate.exchange(
            TASK_API_BASE_URL + "?from=" + from + "&to=" + to,
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            TaskResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        TaskResponse[] tasks = response.getBody();

        assertEquals(2, tasks.length);

        assertTrue(Arrays.stream(tasks)
            .anyMatch(task -> task.title().equals("New single task")));

        assertTrue(Arrays.stream(tasks)
            .anyMatch(task -> task.title().equals("New weekly recur task")));

        assertFalse(Arrays.stream(tasks)
            .anyMatch(task -> task.title().equals("New monthly recur task")));
    }

    @Test
    @Order(5)
    void testGetIdWithSingleTaskSuccess() {
        ResponseEntity<TaskResponse> getResponse = restTemplate.exchange(
            taskIdBaseUrl.formatted(singleTaskId),
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        TaskResponse task = getResponse.getBody();
        assertNotNull(task);
        assertEquals(singleTaskId, task.id());
        assertEquals(USER_ID, task.userId());
        assertEquals(createSingleTaskRequest.title(), task.title());
        assertEquals(createSingleTaskRequest.description(), task.description());
        assertEquals(createSingleTaskRequest.dueDate(), task.dueDate());
        assertEquals(createSingleTaskRequest.rrule(), task.recurrenceRule());
        assertNotNull(task.status());
    }

    @Test
    @Order(6)
    void testGetIdWithWeeklyRecurTaskSuccess() {
        ResponseEntity<TaskResponse> getResponse = restTemplate.exchange(
            taskIdBaseUrl.formatted(weeklyRecurTaskId),
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        TaskResponse task = getResponse.getBody();
        assertNotNull(task);
        assertEquals(weeklyRecurTaskId, task.id());
        assertEquals(USER_ID, task.userId());
        assertEquals(createWeeklyRecurTaskRequest.title(), task.title());
        assertEquals(createWeeklyRecurTaskRequest.description(), task.description());
        assertEquals(createWeeklyRecurTaskRequest.dueDate(), task.dueDate());
        assertEquals(createWeeklyRecurTaskRequest.rrule(), task.recurrenceRule());
        assertNotNull(task.status());
    }

    @Test
    @Order(7)
    void testGetIdWithMonthlyRecurTaskSuccess() {
        ResponseEntity<TaskResponse> getResponse = restTemplate.exchange(
            taskIdBaseUrl.formatted(monthlyRecurTaskId),
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        TaskResponse task = getResponse.getBody();
        assertNotNull(task);
        assertEquals(monthlyRecurTaskId, task.id());
        assertEquals(USER_ID, task.userId());
        assertEquals(createMonthlyRecurTaskRequest.title(), task.title());
        assertEquals(createMonthlyRecurTaskRequest.description(), task.description());
        assertEquals(createMonthlyRecurTaskRequest.dueDate(), task.dueDate());
        assertEquals(createMonthlyRecurTaskRequest.rrule(), task.recurrenceRule());
        assertNotNull(task.status());
    }

    @Test
    @Order(8)
    void testUpdateTaskFromSingleToSingleSuccess() {
        HttpEntity<UpdateTaskRequest> httpEntity = new HttpEntity<>(updateSingleTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(singleTaskId),
            HttpMethod.PUT,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UUID taskId = response.getBody().id();
        assertNotNull(taskId);
        assertEquals(singleTaskId, taskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(updateSingleTaskRequest.title(), response.getBody().title());
        assertEquals(updateSingleTaskRequest.description(), response.getBody().description());
        assertEquals(updateSingleTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(updateSingleTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(updateSingleTaskRequest.status(), response.getBody().status());

        assertTrue(singleTaskRepository.findById(taskId).isPresent());
        assertFalse(recurTaskRepository.findById(taskId).isPresent());

        SingleTaskEntity updatedTask = singleTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(updateSingleTaskRequest.title(), updatedTask.getTitle());
        assertEquals(updateSingleTaskRequest.description(), updatedTask.getDescription());
        assertEquals(updateSingleTaskRequest.dueDate(), updatedTask.getDueDate());
        assertEquals(updateSingleTaskRequest.status(), updatedTask.getStatus());
    }

    @Test
    @Order(9)
    void testUpdateTaskFromRecurToRecurSuccess() {
        HttpEntity<UpdateTaskRequest> httpEntity = new HttpEntity<>(updateRecurTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(monthlyRecurTaskId),
            HttpMethod.PUT,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UUID taskId = response.getBody().id();
        assertNotNull(taskId);
        assertEquals(monthlyRecurTaskId, taskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(updateRecurTaskRequest.title(), response.getBody().title());
        assertEquals(updateRecurTaskRequest.description(), response.getBody().description());
        assertEquals(updateRecurTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(updateRecurTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(updateRecurTaskRequest.status(), response.getBody().status());

        assertTrue(recurTaskRepository.findById(taskId).isPresent());
        assertFalse(singleTaskRepository.findById(taskId).isPresent());

        RecurTaskEntity updatedTask = recurTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(updateRecurTaskRequest.title(), updatedTask.getTitle());
        assertEquals(updateRecurTaskRequest.description(), updatedTask.getDescription());
        assertEquals(updateRecurTaskRequest.rrule(), rruleConverter.toRruleDto(updatedTask.getRrule()));
        assertEquals(rruleConverter.toRruleString(updateRecurTaskRequest.rrule()), updatedTask.getRrule());
        assertEquals(updateRecurTaskRequest.status(), updatedTask.getStatus());
    }

    @Test
    @Order(10)
    void testUpdateTaskFromSingleToRecurSuccess() {
        HttpEntity<UpdateTaskRequest> httpEntity = new HttpEntity<>(updateRecurTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(singleTaskId),
            HttpMethod.PUT,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UUID taskId = response.getBody().id();
        assertNotNull(taskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(updateRecurTaskRequest.title(), response.getBody().title());
        assertEquals(updateRecurTaskRequest.description(), response.getBody().description());
        assertEquals(updateRecurTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(updateRecurTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(updateRecurTaskRequest.status(), response.getBody().status());

        assertTrue(recurTaskRepository.findById(taskId).isPresent());
        assertFalse(singleTaskRepository.findById(taskId).isPresent());

        RecurTaskEntity updatedTask = recurTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(updateRecurTaskRequest.title(), updatedTask.getTitle());
        assertEquals(updateRecurTaskRequest.description(), updatedTask.getDescription());
        assertEquals(updateRecurTaskRequest.rrule(), rruleConverter.toRruleDto(updatedTask.getRrule()));
        assertEquals(rruleConverter.toRruleString(updateRecurTaskRequest.rrule()), updatedTask.getRrule());
        assertEquals(updateRecurTaskRequest.status(), updatedTask.getStatus());
    }

    @Test
    @Order(11)
    void testUpdateTaskFromRecurToSingleSuccess() {
        HttpEntity<UpdateTaskRequest> httpEntity = new HttpEntity<>(updateSingleTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(monthlyRecurTaskId),
            HttpMethod.PUT,
            httpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UUID taskId = response.getBody().id();
        assertNotNull(taskId);
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(updateSingleTaskRequest.title(), response.getBody().title());
        assertEquals(updateSingleTaskRequest.description(), response.getBody().description());
        assertEquals(updateSingleTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(updateSingleTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(updateSingleTaskRequest.status(), response.getBody().status());

        assertTrue(singleTaskRepository.findById(taskId).isPresent());
        assertFalse(recurTaskRepository.findById(taskId).isPresent());

        SingleTaskEntity updatedTask = singleTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(updateSingleTaskRequest.title(), updatedTask.getTitle());
        assertEquals(updateSingleTaskRequest.description(), updatedTask.getDescription());
        assertEquals(updateSingleTaskRequest.dueDate(), updatedTask.getDueDate());
        assertEquals(updateSingleTaskRequest.status(), updatedTask.getStatus());
    }

    @Test
    @Order(12)
    void testUpdateSingleTaskStatusRequest() {
        HttpEntity<CreateTaskRequest> createHttpEntity = new HttpEntity<>(createSingleTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> createResponse = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            createHttpEntity,
            TaskResponse.class
        );

        UUID taskId = createResponse.getBody().id();

        HttpEntity<UpdateTaskStatusRequest> updateHttpEntity = new HttpEntity<>(updateCompleteTaskStatusRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskStatusBaseUrl.formatted(taskId),
            HttpMethod.PATCH,
            updateHttpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(taskId, response.getBody().id());
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(createSingleTaskRequest.title(), response.getBody().title());
        assertEquals(createSingleTaskRequest.description(), response.getBody().description());
        assertEquals(createSingleTaskRequest.dueDate(), response.getBody().dueDate());
        assertEquals(createSingleTaskRequest.rrule(), response.getBody().recurrenceRule());
        assertEquals(updateCompleteTaskStatusRequest.status(), response.getBody().status());

        assertTrue(singleTaskRepository.findById(taskId).isPresent());

        SingleTaskEntity updatedTask = singleTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(createSingleTaskRequest.title(), updatedTask.getTitle());
        assertEquals(createSingleTaskRequest.description(), updatedTask.getDescription());
        assertEquals(createSingleTaskRequest.dueDate(), updatedTask.getDueDate());
        assertEquals(updateCompleteTaskStatusRequest.status(), updatedTask.getStatus());

        singleTaskId = taskId;
    }

    @Test
    @Order(13)
    void testUpdateRecurTaskStatusRequest() {
        HttpEntity<CreateTaskRequest> createHttpEntity = new HttpEntity<>(createWeeklyRecurTaskRequest, httpHeaders);

        ResponseEntity<TaskResponse> createResponse = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            createHttpEntity,
            TaskResponse.class
        );

        UUID taskId = createResponse.getBody().id();

        HttpEntity<UpdateTaskStatusRequest> updateHttpEntity = new HttpEntity<>(updateCompleteTaskStatusRequest, httpHeaders);

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            taskStatusBaseUrl.formatted(taskId),
            HttpMethod.PATCH,
            updateHttpEntity,
            TaskResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(taskId, response.getBody().id());
        assertEquals(USER_ID, response.getBody().userId());
        assertEquals(createWeeklyRecurTaskRequest.title(), response.getBody().title());
        assertEquals(createWeeklyRecurTaskRequest.description(), response.getBody().description());
        assertEquals(createWeeklyRecurTaskRequest.rrule(), response.getBody().recurrenceRule());

        LocalDate expectedDate = rruleCalculator.findNextDate(
            rruleConverter.toRruleString(createWeeklyRecurTaskRequest.rrule()),
            createWeeklyRecurTaskRequest.dueDate()
            );

        assertEquals(expectedDate, response.getBody().dueDate());

        assertTrue(recurTaskRepository.findById(taskId).isPresent());

        RecurTaskEntity updatedTask = recurTaskRepository.findById(taskId).get();
        assertEquals(USER_ID, updatedTask.getUserId());
        assertEquals(createWeeklyRecurTaskRequest.title(), updatedTask.getTitle());
        assertEquals(createWeeklyRecurTaskRequest.description(), updatedTask.getDescription());
        assertEquals(createWeeklyRecurTaskRequest.rrule(), rruleConverter.toRruleDto(updatedTask.getRrule()));
        assertEquals(rruleConverter.toRruleString(createWeeklyRecurTaskRequest.rrule()), updatedTask.getRrule());

        weeklyRecurTaskId = taskId;
    }

    @Test
    @Order(14)
    void testDeleteSingleTaskSuccess() {
        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(singleTaskId),
            HttpMethod.DELETE,
            httpEntity,
            Void.TYPE
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        assertTrue(singleTaskRepository.findById(singleTaskId).isPresent());
        assertEquals(TaskStatus.CANCELLED, singleTaskRepository.findById(singleTaskId).get().getStatus());
    }

    @Test
    @Order(15)
    void testDeleteRecurTaskSuccess() {
        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<Void> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(weeklyRecurTaskId),
            HttpMethod.DELETE,
            httpEntity,
            Void.TYPE
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        assertTrue(recurTaskRepository.findById(weeklyRecurTaskId).isPresent());
        assertEquals(TaskStatus.CANCELLED, recurTaskRepository.findById(weeklyRecurTaskId).get().getStatus());
    }

    @Test
    @Order(16)
    void testValidationExceptionDuringTaskCreation() {
        HttpEntity<CreateTaskRequest> httpEntity = new HttpEntity<>(invalidCreateSingleTaskRequest, httpHeaders);

        ResponseEntity<ValidationErrorResponse> response = restTemplate.exchange(
            TASK_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            ValidationErrorResponse.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ValidationErrorResponse errorResponse = response.getBody();

        assertEquals(400, errorResponse.code());
        assertEquals("MethodArgumentNotValidException", errorResponse.exceptionName());

        assertNotNull(errorResponse.validationErrors());
        assertEquals(3, errorResponse.validationErrors().size());

        assertTrue(errorResponse.validationErrors().stream()
            .anyMatch(error ->
                "title".equals(error.field()) &&
                "Task's title can not be null".equals(error.message())));

        assertTrue(errorResponse.validationErrors().stream()
            .anyMatch(error ->
                "dueDate".equals(error.field()) &&
                "Task's date can not be in past".equals(error.message())));

        assertTrue(errorResponse.validationErrors().stream()
            .anyMatch(error ->
                "title".equals(error.field()) &&
                "Task's title can not be empty".equals(error.message())));

    }

    @Test
    void testNotFoundExceptionHandling() {
        UUID nonExistentTaskId = UUID.randomUUID();

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
            taskIdBaseUrl.formatted(nonExistentTaskId),
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            ApiErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.code());
        assertEquals("TaskNotFoundException", errorResponse.exceptionName());
        assertEquals("Task with id: %s not found".formatted(nonExistentTaskId), errorResponse.exceptionMessage());
    }

}

