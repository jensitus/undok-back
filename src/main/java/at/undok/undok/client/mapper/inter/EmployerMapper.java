package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.EmployerDto;
import at.undok.undok.client.model.entity.Employer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployerMapper {

    EmployerDto toDto(Employer employer);

    Employer toEntity(EmployerDto dto);

    List<EmployerDto> toDtoList(List<Employer> employers);

}
