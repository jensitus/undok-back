package org.service.b.todo.service;

import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.form.DescriptionForm;
import org.service.b.todo.model.Description;

import java.util.List;

public interface DescriptionService {

  List<DescriptionDto> getDescriptionsByItemId(Long item_id);

  DescriptionDto createDescription(DescriptionForm descriptionForm, Long item_id);

  DescriptionDto updateDescription(DescriptionForm descriptionForm, Long item_id);

}
