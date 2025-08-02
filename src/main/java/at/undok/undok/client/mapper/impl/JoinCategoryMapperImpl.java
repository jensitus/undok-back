package at.undok.undok.client.mapper.impl;

import at.undok.undok.client.mapper.annotation.UndokMapper;
import at.undok.undok.client.mapper.inter.JoinCategoryMapper;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.JoinCategoryForm;

@UndokMapper
public class JoinCategoryMapperImpl implements JoinCategoryMapper {

    @Override
    public JoinCategoryDto toDto(JoinCategoryForm joinCategoryForm) {
        return new JoinCategoryDto(joinCategoryForm.getCategoryId(),
                                   joinCategoryForm.getEntityId(),
                                   joinCategoryForm.getCategoryType(),
                                   joinCategoryForm.getEntityType());
    }

    @Override
    public JoinCategoryForm toEntity(JoinCategoryDto dto) {
        return null;
    }

}
