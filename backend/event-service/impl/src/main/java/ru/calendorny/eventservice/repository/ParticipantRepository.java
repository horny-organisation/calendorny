package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
}
