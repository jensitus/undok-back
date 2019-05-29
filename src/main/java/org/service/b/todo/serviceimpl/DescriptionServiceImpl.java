package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.model.User;
import org.service.b.auth.service.UserService;
import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.form.DescriptionForm;
import org.service.b.todo.model.Description;
import org.service.b.todo.model.Item;
import org.service.b.todo.repository.DescriptionRepo;
import org.service.b.todo.repository.ItemRepo;
import org.service.b.todo.service.DescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DescriptionServiceImpl implements DescriptionService {

  private static final Logger logger = LoggerFactory.getLogger(DescriptionServiceImpl.class);

  @Autowired
  private DescriptionRepo descriptionRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ItemRepo itemRepo;

  @Autowired
  private UserService userService;

  @Override
  public List<DescriptionDto> getDescriptionsByItemId(Long item_id) {
    List<Description> descriptionList = descriptionRepo.findByItemId(item_id);
    List<DescriptionDto> descriptionDtoList = new ArrayList<>();
    for (Description description : descriptionList) {
      logger.info("Description for Item {} found: {}", item_id, description);
      descriptionDtoList.add(modelMapper.map(description, DescriptionDto.class));
    }
    return descriptionDtoList;
  }

  @Override
  public DescriptionDto createDescription(DescriptionForm descriptionForm, Long item_id) {
    Description description = new Description(descriptionForm.getText());
    Item item = itemRepo.getOne(item_id);

    description.setCreatedAt(LocalDateTime.now());
    description.setItem(item);
    description.setUserId(userService.getCurrentUser().getId());
    descriptionRepo.save(description);
    return modelMapper.map(description, DescriptionDto.class);
  }

  @Override
  public DescriptionDto updateDescription(DescriptionForm descriptionForm, Long item_id) {
    Description description = descriptionRepo.getOne(descriptionForm.getId());
    description.setText(descriptionForm.getText());
    descriptionRepo.save(description);
    return modelMapper.map(description, DescriptionDto.class);
  }
}
