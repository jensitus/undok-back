package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.service.UserService;
import org.service.b.common.config.ServiceBProcessEnums;
import org.service.b.common.mailer.service.ServiceBOrgMailer;
import org.service.b.common.message.service.MessageService;
import org.service.b.common.model.ModelType;
import org.service.b.common.model.NotifyUsers;
import org.service.b.common.model.WhatIsReported;
import org.service.b.common.processservice.TodoProcessService;
import org.service.b.common.repository.NotifyUsersRepo;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.dto.TodoDto;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

  private static final String NEW_ITEMS_EMAIL_SUBJECT = "new items ";

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

  @Autowired
  private NotifyUsersRepo notifyUsersRepo;

  @Autowired
  private ServiceBOrgMailer serviceBOrgMailer;

  @Override
  public ItemDto createItem(Long todo_id, String name) {
    Todo todo = todoRepo.getOne(todo_id);
    Item item = new Item();
    item.setName(name);
    item.setDone(false);
    item.setCreatedAt(LocalDateTime.now());
    item.setCreatedBy(userService.getCurrentUser().getId());
    item.setTodoId(todo_id);
    Item newItem = itemRepo.save(item);
    ItemDto itemDto = modelMapper.map(newItem, ItemDto.class);
    NotifyUsers notifyUsers = new NotifyUsers();
    notifyUsers.setId(createIdForNotifyingTheUser(itemDto.getId(), itemDto.getName()));
    notifyUsers.setWhatIsReported(WhatIsReported.NEW_ITEM);
    notifyUsers.setModelId(itemDto.getId());
    notifyUsers.setModelType(ModelType.ITEM);
    notifyUsers.setNotified(false);
    notifyUsers.setStringId(createIdForNotifyingTheUser(itemDto.getId(), itemDto.getName()));
    notifyUsersRepo.save(notifyUsers);
    return itemDto;
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
    NotifyUsers notifyUsers = notifyUsersRepo.findByStringId(createIdForNotifyingTheUser(item.getId(), item.getName()));
    if (notifyUsers != null) {
      notifyUsersRepo.delete(notifyUsers);
    }
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
   */
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
  private String createIdForNotifyingTheUser(Long id, String name) {
    String[] donner = name.split(" ");
    String donnerString = "";
    for (int i = 0; i < donner.length; i++) {
      donnerString = donnerString + donner[i];
    }
    return id.toString() + "_" + donnerString;
  }

  @Scheduled(cron = "0 0/10 6-23 * * *")
  @Transactional
  public synchronized void informAboutNewItem() {
    List<ItemDto> itemDtoList = new ArrayList<>();
    Map<Long, List<ItemDto>> itemMap = new HashMap<>();
    List<NotifyUsers> notifyList = notifyUsersRepo.findByModelTypeAndNotified(ModelType.ITEM, false);
    if (!notifyList.isEmpty()) {
      List<ItemDto> itemsToSetNotified = new ArrayList<>();
      itemMap = notifyList.stream().map(n -> getItemDto(n.getModelId())).collect(Collectors.groupingBy(ItemDto::getTodoId));
      Map<TodoDto, List<ItemDto>> todoItemMap = itemMap.entrySet().stream().collect(Collectors.toMap(e -> getTodoDto(e.getKey()), e -> e.getValue()));
      for (TodoDto todoDto : todoItemMap.keySet()) {
        logger.info("todoItemMapKey" + todoDto.toString());
        String subject = ServiceBProcessEnums.SERVICE_B_EMAIL_SUBJECT_PREFIX.getValue() + NEW_ITEMS_EMAIL_SUBJECT + todoDto.getTitle();
        String text = "the following items are new in<br><b>" + todoDto.getTitle() + ":</b>";
        String url = ServiceBProcessEnums.SERVICE_B_BASE_URL.getValue();
        for (ItemDto itemDto : todoItemMap.get(todoDto)) {
          logger.info(itemDto.toString());
          text = text + "<br>&nbsp; - " + itemDto.getName();
          if (itemDto != null) {
            itemsToSetNotified.add(itemDto);
          }
        }
        sendTheMail(todoDto, subject, text, url);
      }
      setNotifiedTrue(itemsToSetNotified);
    }
  }

  private void sendTheMail(TodoDto todoDto, String subject, String text, String url) {
    todoDto.getUsers().forEach(u -> serviceBOrgMailer.getTheMailDetails(u.getEmail(), subject, text, u.getUsername(), url));
  }

  private void setNotifiedTrue(List<ItemDto> itemToSetNotified) {
    for (ItemDto itemDto : itemToSetNotified) {
      NotifyUsers notifyUsers = notifyUsersRepo.findByStringId(createIdForNotifyingTheUser(itemDto.getId(), itemDto.getName()));
      notifyUsers.setNotified(true);
      notifyUsersRepo.save(notifyUsers);
    }
  }

  private ItemDto getItemDto(Long id) {
    Item item = itemRepo.getOne(id);
    return modelMapper.map(item, ItemDto.class);
  }

  private TodoDto getTodoDto(Long id) {
    Todo todo = todoRepo.getOne(id);
    return modelMapper.map(todo, TodoDto.class);
  }

}
