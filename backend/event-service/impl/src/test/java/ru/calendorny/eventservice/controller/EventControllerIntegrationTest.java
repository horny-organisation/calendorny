package ru.calendorny.eventservice.controller;


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
import ru.calendorny.eventservice.dto.ParticipantDto;
import ru.calendorny.eventservice.dto.ReminderDto;
import ru.calendorny.eventservice.dto.error.ApiErrorResponse;
import ru.calendorny.eventservice.dto.error.ValidationErrorResponse;
import ru.calendorny.securitystarter.JwtService;
import ru.calendorny.eventservice.TestContainersConfiguration;
import ru.calendorny.eventservice.data.entity.EventEntity;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import ru.calendorny.eventservice.repository.EventLabelRepository;
import ru.calendorny.eventservice.repository.EventRepository;
import ru.calendorny.eventservice.repository.ParticipantRepository;
import ru.calendorny.eventservice.dto.RruleDto;
import ru.calendorny.eventservice.dto.enums.EventFrequency;
import ru.calendorny.eventservice.dto.enums.MeetingType;
import ru.calendorny.eventservice.dto.enums.ParticipantStatus;
import ru.calendorny.eventservice.dto.request.*;
import ru.calendorny.eventservice.dto.response.*;
import ru.calendorny.eventservice.rrule.RruleEventCalculator;
import ru.calendorny.eventservice.rrule.RruleConverter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(profiles = "test")
@Import({TestContainersConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EventControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventLabelRepository eventLabelRepository;

    @Autowired
    private RruleConverter rruleConverter;

    @Autowired
    private RruleEventCalculator rruleCalculator;

    private String accessToken;
    private HttpHeaders httpHeaders;

    private static Long eventId;
    private static Long recurringEventId;
    private static Long meetingEventId;
    private static Long labelId;

    private static final String EVENT_API_BASE_URL = "/api/v1/events";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_EMAIL = "organizer@mail.ru";
    private static final UUID PARTICIPANT_ID = UUID.randomUUID();
    private static final String PARTICIPANT_EMAIL = "participant@mail.ru";

    private static final CreateEventRequest createEventRequest = new CreateEventRequest(
        "New Event",
        "Event description",
        "Conference room",
        LocalDateTime.now().plusHours(1),
        LocalDateTime.now().plusHours(2),
        null,
        null,
        false,
        null,
        null,
        null
    );

    private static final CreateEventRequest createRecurringEventRequest = new CreateEventRequest(
        "Weekly Meeting",
        "Weekly team meeting",
        "Online",
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
        LocalDateTime.now().plusDays(1).withHour(11).withMinute(0),
        RruleDto.builder()
            .frequency(EventFrequency.WEEKLY)
            .dayOfWeek(DayOfWeek.MONDAY)
            .build(),
        null,
        false,
        null,
        null,
        null
    );

    private static final CreateEventRequest createMeetingEventRequest = new CreateEventRequest(
        "Project Discussion",
        "Discuss project milestones",
        null,
        LocalDateTime.now().plusDays(2).withHour(14).withMinute(0),
        LocalDateTime.now().plusDays(2).withHour(15).withMinute(0),
        null,
        null,
        true,
        MeetingType.GOOGLE,
        List.of(ParticipantDto.builder().userId(PARTICIPANT_ID).email(PARTICIPANT_EMAIL).status(ParticipantStatus.PENDING).build()),
        null
    );

    private static final UpdateEventInfoRequest updateEventInfoRequest = new UpdateEventInfoRequest(
        "Updated Event",
        "Updated description",
        "Updated location",
        LocalDateTime.now().plusHours(3),
        LocalDateTime.now().plusHours(4),
        null,
        null,
        false,
        null,
        null
    );

    private static final UpdateEventReminderRequest updateEventReminderRequest = new UpdateEventReminderRequest(
        new ReminderDto(List.of(15, 30))
    );

    private static final CreateEventRequest invalidCreateEventRequest = new CreateEventRequest(
        null,
        "Desc",
        "Location",
        LocalDateTime.now().minusHours(1),
        LocalDateTime.now().minusHours(2),
        null,
        null,
        false,
        null,
        null,
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
    void testCreateEventSuccess() {
        HttpEntity<CreateEventRequest> httpEntity = new HttpEntity<>(createEventRequest, httpHeaders);

        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            EventDetailedResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        eventId = response.getBody().id();
        assertNotNull(eventId);
        assertEquals(USER_ID, response.getBody().organizerId());
        assertEquals(createEventRequest.title(), response.getBody().title());
        assertEquals(createEventRequest.description(), response.getBody().description());
        assertEquals(createEventRequest.location(), response.getBody().location());
        assertEquals(createEventRequest.start(), response.getBody().startTime());
        assertEquals(createEventRequest.end(), response.getBody().endTime());
        assertEquals(createEventRequest.rrule(), response.getBody().rrule());
        assertEquals(createEventRequest.isMeeting(), response.getBody().isMeeting());
        assertEquals(createEventRequest.meetingType(), response.getBody().meetingType());

        assertTrue(eventRepository.findById(eventId).isPresent());
        EventEntity savedEvent = eventRepository.findById(eventId).get();
        assertEquals(USER_ID, savedEvent.getOrganizerId());
        assertEquals(createEventRequest.title(), savedEvent.getTitle());
        assertEquals(createEventRequest.description(), savedEvent.getDescription());
        assertEquals(createEventRequest.location(), savedEvent.getLocation());
        assertEquals(
            createEventRequest.start().truncatedTo(ChronoUnit.SECONDS),
            savedEvent.getStart().truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(
            createEventRequest.end().truncatedTo(ChronoUnit.SECONDS),
            savedEvent.getEnd().truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(createEventRequest.rrule(), savedEvent.getRrule());
        assertEquals(createEventRequest.isMeeting(), savedEvent.isMeeting());
        assertTrue(savedEvent.isActive());
    }

    @Test
    @Order(2)
    void testCreateRecurringEventSuccess() {
        HttpEntity<CreateEventRequest> httpEntity = new HttpEntity<>(createRecurringEventRequest, httpHeaders);

        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            EventDetailedResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        recurringEventId = response.getBody().id();
        assertNotNull(recurringEventId);
        assertEquals(USER_ID, response.getBody().organizerId());
        assertEquals(createRecurringEventRequest.title(), response.getBody().title());
        assertEquals(createRecurringEventRequest.rrule(), response.getBody().rrule());

        assertTrue(eventRepository.findById(recurringEventId).isPresent());
        EventEntity savedEvent = eventRepository.findById(recurringEventId).get();
        assertEquals(createRecurringEventRequest.rrule(), savedEvent.getRrule());
        assertEquals(rruleConverter.convertToDatabaseColumn(createRecurringEventRequest.rrule()),
            rruleConverter.convertToDatabaseColumn(savedEvent.getRrule()));
    }

    @Test
    @Order(3)
    void testCreateMeetingEventWithParticipantsSuccess() {
        HttpEntity<CreateEventRequest> httpEntity = new HttpEntity<>(createMeetingEventRequest, httpHeaders);

        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            EventDetailedResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        meetingEventId = response.getBody().id();
        assertNotNull(meetingEventId);
        assertEquals(USER_ID, response.getBody().organizerId());
        assertEquals(createMeetingEventRequest.isMeeting(), response.getBody().isMeeting());
        assertEquals(createMeetingEventRequest.meetingType(), response.getBody().meetingType());
        assertNotNull(response.getBody().participants());
        System.out.println(response.getBody().participants().toString());
        assertEquals(1, response.getBody().participants().size());
        assertEquals(PARTICIPANT_ID, response.getBody().participants().get(0).userId());
        assertEquals(ParticipantStatus.PENDING, response.getBody().participants().get(0).status());

        assertTrue(eventRepository.findById(meetingEventId).isPresent());
        EventEntity savedEvent = eventRepository.findById(meetingEventId).get();
        assertEquals(createMeetingEventRequest.isMeeting(), savedEvent.isMeeting());
        assertEquals(createMeetingEventRequest.meetingType(), savedEvent.getMeetingType());
        assertNotNull(savedEvent.getParticipants());
        assertEquals(1, savedEvent.getParticipants().size());
        assertEquals(PARTICIPANT_ID, savedEvent.getParticipants().get(0).getUserId());
        assertEquals(ParticipantStatus.PENDING, savedEvent.getParticipants().get(0).getStatus());
    }

    @Test
    @Order(4)
    void testGetAllEventsByDateRangeSuccess() {
        String from = LocalDateTime.now().minusDays(1).toString();
        String to = LocalDateTime.now().plusDays(3).toString();

        ResponseEntity<EventShortResponse[]> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "?from=" + from + "&to=" + to,
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            EventShortResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        EventShortResponse[] events = response.getBody();

        assertEquals(3, events.length);

        assertTrue(Arrays.stream(events)
            .anyMatch(event -> event.title().equals("New Event")));

        assertTrue(Arrays.stream(events)
            .anyMatch(event -> event.title().equals("Weekly Meeting")));

        assertTrue(Arrays.stream(events)
            .anyMatch(event -> event.title().equals("Project Discussion")));
    }

    @Test
    @Order(5)
    void testGetEventDetailedInfoByIdSuccess() {
        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/" + meetingEventId,
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            EventDetailedResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        EventDetailedResponse event = response.getBody();
        assertNotNull(event);
        assertEquals(meetingEventId, event.id());
        assertEquals(USER_ID, event.organizerId());
        assertEquals(createMeetingEventRequest.title(), event.title());
        assertEquals(createMeetingEventRequest.description(), event.description());
        assertEquals(
            createMeetingEventRequest.start().truncatedTo(ChronoUnit.SECONDS),
            event.startTime().truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(
            createMeetingEventRequest.end().truncatedTo(ChronoUnit.SECONDS),
            event.endTime().truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(createMeetingEventRequest.isMeeting(), event.isMeeting());
        assertEquals(createMeetingEventRequest.meetingType(), event.meetingType());
        assertNotNull(event.participants());
        assertEquals(1, event.participants().size());
    }

    @Test
    @Order(6)
    void testUpdateEventInfoByIdSuccess() {
        HttpEntity<UpdateEventInfoRequest> httpEntity = new HttpEntity<>(updateEventInfoRequest, httpHeaders);

        ResponseEntity<Void> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/" + eventId + "/info",
            HttpMethod.PUT,
            httpEntity,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertTrue(eventRepository.findById(eventId).isPresent());
        EventEntity updatedEvent = eventRepository.findById(eventId).get();
        assertEquals(updateEventInfoRequest.title(), updatedEvent.getTitle());
        assertEquals(updateEventInfoRequest.description(), updatedEvent.getDescription());
        assertEquals(updateEventInfoRequest.location(), updatedEvent.getLocation());
        assertEquals(
            updateEventInfoRequest.start().truncatedTo(ChronoUnit.SECONDS),
            updatedEvent.getStart().truncatedTo(ChronoUnit.SECONDS)
        );
        assertEquals(
            updateEventInfoRequest.end().truncatedTo(ChronoUnit.SECONDS),
            updatedEvent.getEnd().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    @Test
    @Order(7)
    void testUpdateEventReminderByIdSuccess() {
        // Сначала создаем событие с напоминанием
        CreateEventRequest requestWithReminder = new CreateEventRequest(
            "Event with reminder",
            null,
            null,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            null,
            null,
            false,
            null,
            null,
            new ReminderDto(List.of(60))
        );

        HttpEntity<CreateEventRequest> createHttpEntity = new HttpEntity<>(requestWithReminder, httpHeaders);
        ResponseEntity<EventDetailedResponse> createResponse = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            createHttpEntity,
            EventDetailedResponse.class
        );
        Long eventWithReminderId = createResponse.getBody().id();

        // Обновляем напоминание
        HttpEntity<UpdateEventReminderRequest> updateHttpEntity = new HttpEntity<>(updateEventReminderRequest, httpHeaders);
        ResponseEntity<Void> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/" + eventWithReminderId + "/reminder",
            HttpMethod.PATCH,
            updateHttpEntity,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Order(8)
    void testGetAllEventInvitationsSuccess() {
        accessToken = jwtService.generateAccessToken(
            PARTICIPANT_ID.toString(),
            Map.of(
                "email", PARTICIPANT_EMAIL,
                "id", PARTICIPANT_ID)
        );

        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<EventDetailedResponse[]> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/invitations",
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            EventDetailedResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(meetingEventId, response.getBody()[0].id());
    }

    @Test
    @Order(9)
    void testAnswerInvitationSuccess() {
        accessToken = jwtService.generateAccessToken(
            PARTICIPANT_ID.toString(),
            Map.of(
                "email", PARTICIPANT_EMAIL,
                "id", PARTICIPANT_ID)
        );

        httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ParticipantEntity participant = participantRepository.findByUserIdAndEvent_Id(PARTICIPANT_ID, meetingEventId)
            .orElseThrow();
        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/invitations/" + meetingEventId + "?answer=ACCEPTED",
            HttpMethod.POST,
            new HttpEntity<>(httpHeaders),
            EventDetailedResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(meetingEventId, response.getBody().id());
        assertNotNull(response.getBody().participants());
        assertEquals(1, response.getBody().participants().size());
        assertEquals(ParticipantStatus.ACCEPTED, response.getBody().participants().get(0).status());

        ParticipantEntity updatedParticipant = participantRepository.findById(participant.getId()).orElseThrow();
        assertEquals(ParticipantStatus.ACCEPTED, updatedParticipant.getStatus());
    }

    @Test
    @Order(10)
    void testDeleteEventByIdSuccess() {
        // Создаем новое событие для удаления
        CreateEventRequest eventToDeleteRequest = new CreateEventRequest(
            "Event to delete",
            null,
            null,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            null,
            null,
            false,
            null,
            null,
            null
        );

        HttpEntity<CreateEventRequest> createHttpEntity = new HttpEntity<>(eventToDeleteRequest, httpHeaders);
        ResponseEntity<EventDetailedResponse> createResponse = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            createHttpEntity,
            EventDetailedResponse.class
        );
        Long eventToDeleteId = createResponse.getBody().id();

        // Удаляем событие
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            EVENT_API_BASE_URL + "/" + eventToDeleteId,
            HttpMethod.DELETE,
            new HttpEntity<>(httpHeaders),
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Проверяем, что событие помечено как неактивное
        EventEntity deletedEvent = eventRepository.findById(eventToDeleteId).orElseThrow();
        assertFalse(deletedEvent.isActive());
    }

    @Test
    @Order(11)
    void testNotFoundExceptionHandling() {
        Long nonExistentEventId = 9999L;

        ResponseEntity<ApiErrorResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL + "/" + nonExistentEventId,
            HttpMethod.GET,
            new HttpEntity<>(httpHeaders),
            ApiErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.code());
        assertEquals("NotFoundException", errorResponse.exceptionName());
        assertEquals("Event with id: " + nonExistentEventId + " not found", errorResponse.exceptionMessage());
    }

    @Test
    @Order(12)
    void testCreateEventWithLabelsSuccess() {
        // Сначала создаем метку
        EventLabelEntity label = EventLabelEntity.builder()
            .name("Important")
            .color("#FF0000")
            .build();
        label = eventLabelRepository.save(label);
        labelId = label.getId();

        // Создаем событие с меткой
        CreateEventRequest requestWithLabel = new CreateEventRequest(
            "Event with label",
            null,
            null,
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2),
            null,
            List.of(labelId),
            false,
            null,
            null,
            null
        );

        HttpEntity<CreateEventRequest> httpEntity = new HttpEntity<>(requestWithLabel, httpHeaders);
        ResponseEntity<EventDetailedResponse> response = restTemplate.exchange(
            EVENT_API_BASE_URL,
            HttpMethod.POST,
            httpEntity,
            EventDetailedResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().labels());
        assertEquals(1, response.getBody().labels().size());
        assertEquals(labelId, response.getBody().labels().get(0).id());
    }
}
