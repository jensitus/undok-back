package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.service.UserService;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.model.Item;
import org.service.b.todo.model.Todo;
import org.service.b.todo.repository.ItemRepo;
import org.service.b.todo.repository.TodoRepo;
import org.service.b.todo.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ItemServiceImpl implements ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

  @Autowired
  private TodoRepo todoRepo;

  @Autowired
  private ItemRepo itemRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserService userService;

  @Override
  public Item createItem(Long todo_id, String name) {
    logger.info(name + " " + todo_id);
    Todo todo = todoRepo.getOne(todo_id);
    Item item = new Item();
    item.setName(name);
    item.setDone(false);
    item.setCreatedAt(LocalDateTime.now());
    item.setCreatedBy(userService.getCurrentUser().getId());
    item.setTodo_id(todo_id);
    itemRepo.save(item);
    return item;
  }

  @Override
  public ItemDto updateItem(Long item_id) {
    Item item = itemRepo.getOne(item_id);
    boolean done = !item.isDone();
    item.setDone(done);
    itemRepo.save(item);
    return modelMapper.map(item, ItemDto.class);
  }

  @Override
  public void deleteItem(Long item_id) {
    Item item = itemRepo.getOne(item_id);
    itemRepo.delete(item);
  }
}
