package org.service.b.todo.serviceimpl;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.UserService;
import org.service.b.common.message.Message;
import org.service.b.common.message.service.MessageService;
import org.service.b.common.message.service.ServiceBCamundaUserService;
import org.service.b.common.message.service.ServiceBProcessService;
import org.service.b.common.processservice.TodoProcessService;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.model.Item;
import org.service.b.todo.model.Todo;
import org.service.b.todo.repository.ItemRepo;
import org.service.b.todo.repository.TodoRepo;
import org.service.b.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TodoServiceImpl implements TodoService {

  private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

  @Autowired
  private TodoRepo todoRepo;

  @Autowired
  private ItemRepo itemRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private UserService userService;

  @Autowired
  private TodoProcessService todoProcessService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private ServiceBCamundaUserService serviceBCamundaUserService;

  @Autowired
  private ServiceBProcessService serviceBProcessService;

  @Override
  public TodoDto createTodo(String title) {
    Todo todo = new Todo(title);
    UserDto userDto = userService.getCurrentUser();
    logger.info("currentUser" + userDto.getUsername());
    todo.setCreatedBy(userDto.getId());
    todo.setCreated_at(LocalDateTime.now());
    Set<User> users = new HashSet<>();
    users.add(modelMapper.map(userDto, User.class));
    todo.setUsers(users);
    Todo newTodo = todoRepo.save(todo);
    todoProcessService.startTodo(newTodo.getId(), userDto);
    return modelMapper.map(newTodo, TodoDto.class);
  }

  @Override
  public List<TodoDto> getTodos() {
    UserDto userDto = userService.getCurrentUser();
    User user = userRepo.findByEmail(userDto.getEmail());
    Set<Todo> todoSet = user.getTodos();
    ArrayList<TodoDto> todoDtoList = new ArrayList<>();
    for (Todo todo : todoSet) {
      TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
      todoDtoList.add(todoDto);
    }
    Collections.sort(todoDtoList, (TodoDto a, TodoDto b) -> a.getId().compareTo(b.getId()));
    return todoDtoList;
  }

  @Override
  public TodoDto getTodoById(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    Set<User> userSet = todo.getUsers();
    Set<UserDto> userDtoSet = new HashSet<>();
    for (User user : userSet) {
      userDtoSet.add(modelMapper.map(user, UserDto.class));
    }
    Set<Item> itemSet = todo.getItems();
    List<Item> itemList = itemRepo.findByTodoIdOrderByCreatedAt(todo_id);
    List<ItemDto> itemDtoList = new ArrayList<>();
    for (Item item : itemList) {
      itemDtoList.add(modelMapper.map(item, ItemDto.class));
    }
    TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
    todoDto.setUsers(userDtoSet);
    todoDto.setItems(itemDtoList);
    return todoDto;
  }

  @Override
  public List getTodoItems(Long todo_id) {
    // Todo todo = todoRepo.getOne(todo_id);
    List<Item> itemList = itemRepo.findByTodoIdOrderByCreatedAt(todo_id);
    List<ItemDto> itemDtos = new ArrayList();
    // Set<Item> items = todo.getItems();
    for (Item item : itemList) {
      itemDtos.add(modelMapper.map(item, ItemDto.class));
    }
    return itemDtos;
  }

  @Override
  public List<UserDto> getTodoUsers(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    Set<User> userSet = todo.getUsers();
    List<UserDto> userDtoList = new ArrayList<>();
    for (User user : userSet) {
      userDtoList.add(modelMapper.map(user, UserDto.class));
    }
    return userDtoList;
  }

  @Override
  public TodoDto addUserToTodo(Long todo_id, Long user_id) {
    Todo todo = todoRepo.getOne(todo_id);
    User user = userRepo.getOne(user_id);
    Set<User> userSet = new HashSet<>();
    userSet = todo.getUsers();
    userSet.add(user);
    todo.setUsers(userSet);
    todoRepo.save(todo);
    serviceBCamundaUserService.addUserToCamundaGroup(user_id.toString(), todo_id, "todo");
    return modelMapper.map(todo, TodoDto.class);
  }

  @Override
  public TodoDto updateTodo(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    todo.setDone(!todo.isDone());
    todoRepo.save(todo);
    return modelMapper.map(todo, TodoDto.class);
  }

  @Override
  public Message todoFinished(Long todo_id) {
    TodoDto todoDto = getTodoById(todo_id);
    if (todoDto.getItems().isEmpty()) {
      messageService.sendMessageToCatchEvent("todo-finished", "service-b-todo", todo_id);
      return new Message("Todo successfully deleted", true);
    } else {
      messageService.sendMessageToCatchEvent("todo-finished", "service-b-todo", todo_id);
      return new Message("There is still so much to do", false);
    }
  }

  @Override
  public Message deleteTodo(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    todoRepo.delete(todo);
    return new Message("yepp", false);
  }

  @Override
  public boolean checkOpenItems(String task_id) {
    ProcessInstance processInstance = serviceBProcessService.getProcessInstanceByTask(task_id);
    List<ProcessInstance> pis = serviceBProcessService.getProcessInstancesByBusinessKey(processInstance.getBusinessKey());
    boolean openItems = true;
    if (pis.size() > 1) {
      openItems = true;
    } else if (pis.size() == 1) {
      openItems = false;
    } else {
      openItems = false;
    }
    return openItems;
  }
}
