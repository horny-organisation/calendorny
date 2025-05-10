package ru.calendorny.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecurTaskRepository extends JpaRepository<RecurTaskEntity, UUID> {

    Optional<RecurTaskEntity> findById(UUID taskId);
    List<RecurTaskEntity> findAllByUserId(UUID userId);
}
