package ru.calendorny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.data.entity.ParticipantEntity;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, UUID> {
}
