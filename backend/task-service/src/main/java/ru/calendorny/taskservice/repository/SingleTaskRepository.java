package ru.calendorny.taskservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.calendorny.taskservice.entity.SingleTaskEntity;

public interface SingleTaskRepository extends JpaRepository<SingleTaskEntity, UUID> {

    @Query(
        """
            SELECT t FROM SingleTaskEntity t
            WHERE t.userId = :userId
            AND t.status IN (ru.calendorny.taskservice.enums.TaskStatus.PENDING, ru.calendorny.taskservice.enums.TaskStatus.COMPLETED)
            AND t.dueDate BETWEEN :startDate AND :endDate
            ORDER BY t.dueDate ASC
            LIMIT 100
            """)
    List<SingleTaskEntity> findAllActiveByUserIdAndDateInterval(
            @Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM SingleTaskEntity t WHERE t.status = 'PENDING' AND t.dueDate = :date")
    List<SingleTaskEntity> findAllPendingByDueDate(LocalDate dueDate);
}
