package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.calendorny.eventservice.data.entity.EventEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query(
        """
               SELECT DISTINCT e FROM EventEntity e
               JOIN e.participants p
               WHERE e.isActive = true
               AND p.userId = :userId
               AND p.status = ru.calendorny.eventservice.dto.enums.ParticipantStatus.PENDING
            """)
    List<EventEntity> getAllPendingToUserEvents(@Param("userId") UUID userId);

    @Query("""
           SELECT e FROM EventEntity e
           LEFT JOIN e.participants p
           WHERE e.rrule IS NULL
           AND (e.start BETWEEN :rangeStart AND :rangeEnd OR e.end BETWEEN :rangeStart AND :rangeEnd)
           AND e.isActive = true
           AND e.organizerId = :userId OR p.userId = :userId
        """)
    List<EventEntity> findAllSimpleEventsInRange(
        @Param("userId") UUID userId,
        @Param("rangeStart") LocalDateTime rangeStart,
        @Param("rangeEnd") LocalDateTime rangeEnd
    );

    @Query("""
           SELECT e FROM EventEntity e
           LEFT JOIN e.participants p
           WHERE e.rrule IS NOT NULL
           AND e.isActive = true
           AND e.organizerId = :userId OR p.userId = :userId
        """)
    List<EventEntity> findAllRecurEventsInRange(@Param("userId") UUID userId);

    @Query("""
            SELECT DISTINCT e FROM EventEntity e
            LEFT JOIN e.participants p
            WHERE e.isActive = true
              AND (e.organizerId = :userId OR p.userId = :userId)
              AND (
                e.rrule IS NOT NULL OR
                (e.start BETWEEN :start AND :end)
              )
        """)
    List<EventEntity> findRelevantEvents(
        @Param("userId") UUID userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );


}
