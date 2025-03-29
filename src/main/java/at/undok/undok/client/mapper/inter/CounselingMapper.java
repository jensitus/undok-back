package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Counseling;

public interface CounselingMapper extends Mapper<Counseling, CounselingDto> {

    @Override
    CounselingDto toDto(Counseling counseling);

    @Override
    Counseling toEntity(CounselingDto dto);
}
