package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {

    List<ReminderEntity> findByEventId(Long eventId);

    List<ReminderEntity> findByEventIdAndUserId(Long eventId, UUID userId);
}
