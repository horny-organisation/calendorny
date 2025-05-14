package ru.calendorny.taskservice.dto.event;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Setter;

@Setter
public class TodayTaskEvent {

    private UUID taskId;

    private UUID userId;

    private String title;

    private String description;

    private LocalDate dueDate;
}
