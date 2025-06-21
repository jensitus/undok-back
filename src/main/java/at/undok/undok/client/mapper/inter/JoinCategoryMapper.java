package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.JoinCategoryForm;

public interface JoinCategoryMapper extends Mapper<JoinCategoryForm, JoinCategoryDto> {

    @Override
    JoinCategoryDto toDto(JoinCategoryForm joinCategoryForm);

}
