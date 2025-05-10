package ru.calendorny.taskservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import ru.calendorny.taskservice.enums.TaskStatus;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recurrence_tasks")
public class RecurTaskEntity {

    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;


    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Column(name = "rrule", nullable = false)
    private String rrule;

    @Column(name = "next_date", nullable = false)
    private LocalDate nextDate;
}
