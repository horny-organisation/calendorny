package ru.calendorny.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.calendorny.notificationservice.entity.EventReminderRequest;
import ru.calendorny.notificationservice.entity.TodayTaskEvent;
import ru.calendorny.notificationservice.handler.NotificationsHandler;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationsHandler updatesHandler;

    @RetryableTopic(
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = ".dlt",
        dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
        topics = "${kafka.task-notification-topic}",
        groupId = "${kafka.group-id}",
        containerFactory = "kafkaListenerContainerFactoryTask"
    )
    public void handleTaskUpdates(TodayTaskEvent request) {
        log.info(
            "Received new notification request. TaskId={}, UserId={}, Title={}, Description={}, DueDate={}",
            request.taskId(),
            request.userId(),
            request.title(),
            request.description(),
            request.dueDate());

        updatesHandler.handleTaskUpdates(request);
    }

    @RetryableTopic(
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = ".dlt",
        dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
        topics = "${kafka.event-notification-topic}",
        groupId = "${kafka.group-id}",
        containerFactory = "kafkaListenerContainerFactoryEvent"
    )
    public void handleEventUpdates(EventReminderRequest request) {
        log.info(
            "Received new notification request. EventId={}, UserId={}, Title={}, Location={}, StartTime={}, EndTime={}",
            request.eventId(),
            request.userId(),
            request.title(),
            request.location(),
            request.start(),
            request.end());

        updatesHandler.handleEventUpdates(request);
    }
}
