package ru.calendorny.taskservice.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.calendorny.taskservice.TestContainersConfiguration;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.kafka.properties.KafkaConfigProperties;
import ru.calendorny.taskservice.mapper.TaskMapper;
import ru.calendorny.taskservice.service.impl.RecurTaskProcessor;
import ru.calendorny.taskservice.service.impl.SingleTaskProcessor;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static java.time.ZoneOffset.UTC;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles(profiles = "test")
@Import(TestContainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DailyKafkaProducerTest {

    @Autowired
    private DailyKafkaProducerTask dailyKafkaProducerTask;

    @MockitoBean
    private SingleTaskProcessor singleTaskProcessor;

    @MockitoBean
    private RecurTaskProcessor recurTaskProcessor;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private KafkaConfigProperties kafkaConfigProperties;

    private static final UUID TASK_ID = UUID.randomUUID();

    private static final UUID USER_ID = UUID.randomUUID();

    private static final TaskResponse taskResponse = new TaskResponse(TASK_ID, USER_ID, "Title", "Desc",
        LocalDate.now(), TaskStatus.PENDING, null);
    private static final TodayTaskEvent todayTaskEvent = new TodayTaskEvent(TASK_ID, USER_ID, "Title",
        "Desc", LocalDate.now());

    @Test
    void testRunAtMidnightUTC_sendsKafkaEvents() {
        when(singleTaskProcessor.getPendingTasksByDate(LocalDate.now(UTC)))
            .thenReturn(List.of(taskResponse));
        when(recurTaskProcessor.getPendingTasksByDate(LocalDate.now(UTC)))
            .thenReturn(Collections.emptyList());
        when(taskMapper.fromResponseToEvent(taskResponse)).thenReturn(todayTaskEvent);

        dailyKafkaProducerTask.runAtMidnightUTC();

        verify(kafkaTemplate, times(1)).send(kafkaConfigProperties.taskNotificationTopic(), todayTaskEvent);
    }

}
