package at.undok.undok.client.mapper.impl;

import at.undok.undok.client.mapper.annotation.UndokMapper;
import at.undok.undok.client.mapper.inter.CaseMapper;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.entity.Case;

@UndokMapper
public class CaseMapperImpl implements CaseMapper {

    @Override
    public CaseDto toDto(Case entity) {
        return new CaseDto(entity.getId(),
                           entity.getCreatedAt().toString(),
                           entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null,
                           entity.getName(),
                           entity.getStatus(),
                           entity.getStartDate(),
                           entity.getEndDate(),
                           entity.getReferredTo(),
                           entity.getClientId(),
                           entity.getTotalConsultationTime(),
                           entity.getTargetGroup()
        );
    }

    @Override
    public Case toEntity(CaseDto dto) {
        return new Case(dto.getName(),
                        dto.getStatus(),
                        dto.getClientId());
    }

}
