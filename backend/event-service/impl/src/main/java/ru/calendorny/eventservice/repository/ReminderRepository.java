package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.ReminderEntity;
import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {

    List<ReminderEntity> findAllByEvent_IdAndUserId(Long eventId, UUID userId);

    List<ReminderEntity> findAllByEvent_Id(Long eventId);
}
