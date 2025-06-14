package ru.calendorny.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.calendorny.dto.RruleDto;
import ru.calendorny.dto.enums.MeetingType;
import ru.calendorny.util.rrule.RruleConverter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Convert(converter = RruleConverter.class)
    @Column(name = "rrule")
    private RruleDto rrule;

    @Column(name = "is_meeting")
    private boolean isMeeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type")
    private MeetingType meetingType;

    @Column(name = "video_meeting_url")
    private String videoMeetingUrl;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "event")
    private List<ParticipantEntity> participants;

    @Column(name = "organizer_id")
    private UUID organizerId;

    @ManyToMany
    @JoinTable(
        name = "event_label_links",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<EventLabelEntity> labels;

    @OneToMany(mappedBy = "event")
    private List<ReminderEntity> reminders;

}
