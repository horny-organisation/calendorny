package ru.calendorny.taskservice.util;

import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.enums.TaskStatus;
import ru.calendorny.taskservice.repository.SingleTaskRepository;

@Component
@RequiredArgsConstructor
public class SingleTaskHelper {

    private final SingleTaskRepository repository;

    public void createCompletedSingleTask(String title, String description, UUID userId, LocalDate dueDate) {
        repository.save(SingleTaskEntity.builder()
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .status(TaskStatus.COMPLETED)
                .userId(userId)
                .build());
    }
}
