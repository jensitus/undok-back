package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.UserService;
import org.service.b.auth.serviceimpl.UserPrinciple;
import org.service.b.common.message.Message;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    Set<Todo> todoSet = user.getTodos();
    todoSet.add(todo);
    userRepo.save(user);
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
  public Message deleteTodo(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    logger.info("todo to delete " + todo);
    logger.info(todo.getId().toString());
    logger.info(todo.getTitle());
    logger.info(todo.getItems().toString());
    if (todo.getItems().isEmpty()) {
      todoRepo.delete(todo);
      return new Message("Todo successfully deleted", true);
    } else {
      return new Message("There is still so much to do", false);
    }
  }
}
