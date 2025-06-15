package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.ParticipantEntity;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {

    Optional<ParticipantEntity> findByUserIdAndEvent_Id(UUID userId, Long eventId);
}
