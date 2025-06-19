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
        value = """
                SELECT * FROM single_tasks
                WHERE user_id = :userId
                  AND status IN ('PENDING', 'COMPLETED')
                  AND due_date BETWEEN :startDate AND :endDate
                ORDER BY due_date ASC
                LIMIT :limit
            """,
        nativeQuery = true
    )
    List<SingleTaskEntity> findAllActiveByUserIdAndDateInterval(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("limit") int limit
    );

    @Query(
        value = """
                SELECT * FROM single_tasks
                WHERE status = 'PENDING'
                  AND due_date = :date
            """,
        nativeQuery = true
    )
    List<SingleTaskEntity> findAllPendingByDueDate(@Param("date") LocalDate dueDate);
}
