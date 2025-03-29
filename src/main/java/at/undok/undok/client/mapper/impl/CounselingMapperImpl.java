package at.undok.undok.client.mapper.impl;

import at.undok.undok.client.mapper.annotation.UndokMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Counseling;

@UndokMapper
public class CounselingMapperImpl implements CounselingMapper {

    @Override
    public CounselingDto toDto(Counseling counseling) {
        return new CounselingDto(counseling.getId(),
                                 counseling.getConcern(),
                                 counseling.getActivity(),
                                 counseling.getRegisteredBy(),
                                 counseling.getCounselingDate() != null ? counseling.getCounselingDate().toString() : null,
                                 counseling.getCreatedAt(),
                                 counseling.getComment(),
                                 counseling.getClient().getId(),
                                 counseling.getClient().getPerson().getFirstName() + " " + counseling.getClient().getPerson().getLastName(),
                                 counseling.getClient().getKeyword(),
                                 counseling.getRequiredTime());
    }

    @Override
    public Counseling toEntity(CounselingDto dto) {
        return null;
    }

}
