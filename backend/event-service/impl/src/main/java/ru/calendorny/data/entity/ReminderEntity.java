package ru.calendorny.data.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "minutes_before", nullable = false)
    private Integer minutesBefore;

    @ManyToOne
    @JoinColumn(name = "reminder_method", nullable = false)
    private ReminderMethodEntity reminderMethod;
}
