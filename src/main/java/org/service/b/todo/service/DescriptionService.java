package org.service.b.todo.service;

import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.form.DescriptionForm;

import java.util.List;

public interface DescriptionService {

  List<DescriptionDto> getDescriptionsByItemId(Long item_id);

  DescriptionDto createDescription(DescriptionForm descriptionForm, Long item_id);

}
