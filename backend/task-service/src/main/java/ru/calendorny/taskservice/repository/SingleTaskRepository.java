package ru.calendorny.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SingleTaskRepository extends JpaRepository<SingleTaskEntity, UUID> {

    Optional<SingleTaskEntity> findById (UUID taskId);

    boolean existsById (UUID taskId);

    @Query("SELECT t FROM SingleTaskEntity t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate")
    List<SingleTaskEntity> findAllByUserIdAndDueDateBetween(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
