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
        """
            SELECT t FROM RecurTaskEntity t
            WHERE t.userId = :userId
            AND t.status IN (ru.calendorny.taskservice.enums.TaskStatus.PENDING, ru.calendorny.taskservice.enums.TaskStatus.COMPLETED)
            AND t.nextDate BETWEEN :startDate AND :endDate
            ORDER BY t.nextDate ASC
            LIMIT 100
        """
    )
    List<RecurTaskEntity> findAllActiveByUserIdAndDateInterval(
        @Param("userId") UUID userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query(
        """
                 SELECT t FROM RecurTaskEntity t
                 WHERE t.status = 'PENDING' AND t.nextDate = :date
        """
    )
    List<RecurTaskEntity> findAllPendingByNextDate(LocalDate nextDate);
}
