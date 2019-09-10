package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.service.UserService;
import org.service.b.common.message.service.MessageService;
import org.service.b.common.processservice.TodoProcessService;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.model.Description;
import org.service.b.todo.model.Item;
import org.service.b.todo.model.Todo;
import org.service.b.todo.repository.DescriptionRepo;
import org.service.b.todo.repository.ItemRepo;
import org.service.b.todo.repository.TodoRepo;
import org.service.b.todo.service.DescriptionService;
import org.service.b.todo.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

  @Autowired
  private DescriptionRepo descriptionRepo;

  @Autowired
  private DescriptionService descriptionService;

  @Autowired
  private TodoProcessService todoProcessService;

  @Autowired
  private MessageService messageService;

  @Override
  public Item createItem(Long todo_id, String name) {
    Todo todo = todoRepo.getOne(todo_id);
    Item item = new Item();
    item.setName(name);
    item.setDone(false);
    item.setCreatedAt(LocalDateTime.now());
    item.setCreatedBy(userService.getCurrentUser().getId());
    item.setTodoId(todo_id);
    Item newItem = itemRepo.save(item);
    // todoProcessService.startSubTodoServiceItem(todo_id, newItem.getId());
    // messageService.sendMessageToCatchEvent("start-sub-item", "service-b-todo", todo_id);
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
    logger.info(item.toString());
    List<Description> itemDescriptions = descriptionRepo.findByItemIdOrderByCreatedAt(item.getId());
    descriptionRepo.deleteInBatch(itemDescriptions);
    itemRepo.delete(item);

  }

  @Override
  public ItemDto setItemDueDate(Long item_id, String dueDate) {
    logger.info(dueDate.toString());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate formattedDueDate = LocalDate.parse(dueDate, formatter);
    Item item = itemRepo.getOne(item_id);
    item.setDueDate(formattedDueDate);
    itemRepo.save(item);
    ItemDto itemDto = modelMapper.map(item, ItemDto.class);
    logger.info(itemDto.toString());
    return itemDto;
  }

  /**
  * We will need the scheduler below in future versions
  * */
//  @Scheduled(cron = "0 34 14 * * *")
//  private void checkFixedRateTask() {
//    logger.info("fix the Rate " + LocalDateTime.now());
//    LocalDate dueDateStart = LocalDate.now();
//    LocalDate dueDateEnd = LocalDate.now().plusDays(2);
//    getDueDatedItems(dueDateStart, dueDateEnd);
//  }
//
//  private void getDueDatedItems(LocalDate dueDateStart, LocalDate dueDateEnd) {
//    List<Item> itemList = itemRepo.findByDueDateBetween(dueDateStart, dueDateEnd);
//    for (Item item : itemList) {
//      logger.info(item.toString());
//    }
//  }

}
