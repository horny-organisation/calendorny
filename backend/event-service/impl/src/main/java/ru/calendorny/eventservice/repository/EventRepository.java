package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.EventEntity;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
}
