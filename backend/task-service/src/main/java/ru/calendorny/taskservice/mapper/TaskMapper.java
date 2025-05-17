package ru.calendorny.taskservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.calendorny.taskservice.dto.event.TodayTaskEvent;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import ru.calendorny.taskservice.util.RruleConverter;

@Mapper(componentModel = "spring", uses = RruleConverter.class)
public interface TaskMapper {

    @Mappings({
        @Mapping(target = "dueDate", source = "nextDate"),
        @Mapping(target = "recurrenceRule", source = "rrule")
    })
    TaskResponse fromRecurTaskToResponse(RecurTaskEntity recurTaskEntity);

    @Mappings({@Mapping(target = "recurrenceRule", ignore = true)})
    TaskResponse fromSingleTaskToResponse(SingleTaskEntity singleTaskEntity);

    @Mappings({@Mapping(target = "taskId", source = "id")})
    TodayTaskEvent fromResponseToEvent(TaskResponse taskResponse);
}
