package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;

public interface CaseMapper extends Mapper<Case, CaseDto> {

    CaseDto toDto(Case entity);

    Case toEntity(CaseDto dto);
}
