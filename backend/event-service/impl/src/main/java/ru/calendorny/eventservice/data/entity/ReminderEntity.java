package ru.calendorny.eventservice.data.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reminders")
public class ReminderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "minutes_before", nullable = false)
    private Integer minutesBefore;

    @Column(name = "notification_job_id")
    private UUID notificationJobId;

    @Column(name = "user_id")
    private UUID userId;
}
