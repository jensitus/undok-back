package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.TaskDto;
import at.undok.undok.client.model.entity.Task;
import org.mapstruct.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "caseEntity.id", target = "caseId")
    @Mapping(source = "caseEntity.clientId", target = "clientId")
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    TaskDto toDto(Task entity);

    @Mapping(target = "caseEntity", ignore = true)
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    Task toEntity(TaskDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "caseEntity", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dueDate", target = "dueDate", dateFormat = "yyyy-MM-dd")
    void updateEntityFromDto(TaskDto dto, @MappingTarget Task entity);
}
