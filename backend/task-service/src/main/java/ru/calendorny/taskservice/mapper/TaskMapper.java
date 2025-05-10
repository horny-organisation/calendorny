package ru.calendorny.taskservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.calendorny.taskservice.dto.response.TaskResponse;
import ru.calendorny.taskservice.entity.RecurTaskEntity;
import ru.calendorny.taskservice.entity.SingleTaskEntity;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mappings({
        @Mapping(target = "dueDate", source = "nextDate"),
        @Mapping(target = "recurrenceRule", expression = "java(ru.calendorny.taskservice.util.RruleConverter.toRruleDto(recurTaskEntity.getRrule()))")
    })
    TaskResponse fromRecurTaskToResponse(RecurTaskEntity recurTaskEntity);

    @Mappings({
        @Mapping(target = "dueDate", source = "nextDate"),
        @Mapping(target = "recurrenceRule", expression = "java(ru.calendorny.taskservice.util.RruleConverter.toRruleDto(recurTaskEntity.getRrule()))")
    })
    TaskResponse fromRecurTaskToResponse(RecurTaskEntity recurTaskEntity, LocalDate nextDate);

    @Mappings({
        @Mapping(target = "recurrenceRule", ignore = true)
    })
    TaskResponse fromSingleTaskToResponse(SingleTaskEntity singleTaskEntity);
}
