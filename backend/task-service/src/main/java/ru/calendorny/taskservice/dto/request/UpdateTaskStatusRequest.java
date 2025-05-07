package ru.calendorny.taskservice.dto.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.calendorny.taskservice.enums.TaskStatus;

@Getter
@EqualsAndHashCode
public class UpdateTaskStatusRequest {

    private TaskStatus status;
}
