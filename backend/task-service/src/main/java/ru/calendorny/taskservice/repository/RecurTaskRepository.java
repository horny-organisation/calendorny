package ru.calendorny.taskservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.calendorny.taskservice.entity.RecurTaskEntity;

public interface RecurTaskRepository extends JpaRepository<RecurTaskEntity, UUID> {

    @Query(
        value = """
                SELECT * FROM recurrence_tasks
                WHERE user_id = :userId
                  AND status IN ('PENDING', 'COMPLETED')
                  AND next_date BETWEEN :startDate AND :endDate
                ORDER BY next_date ASC
                LIMIT :limit
            """,
        nativeQuery = true
    )
    List<RecurTaskEntity> findAllActiveByUserIdAndDateInterval(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("limit") int limit
    );


    @Query(
        """
                 SELECT t FROM RecurTaskEntity t
                 WHERE t.status = 'PENDING' AND t.nextDate = :date
        """
    )
    List<RecurTaskEntity> findAllPendingByNextDate(LocalDate nextDate);
}
