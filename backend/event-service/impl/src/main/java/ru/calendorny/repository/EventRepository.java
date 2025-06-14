package ru.calendorny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.data.entity.EventEntity;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
