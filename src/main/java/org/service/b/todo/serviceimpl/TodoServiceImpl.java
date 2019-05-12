package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.UserService;
import org.service.b.auth.serviceimpl.UserPrinciple;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.model.Item;
import org.service.b.todo.model.Todo;
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
  private ModelMapper modelMapper;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private UserService userService;

  @Override
  public Todo createTodo(String title) {
    Todo todo = new Todo(title);
    UserDto userDto = userService.getCurrentUser();
    todo.setCreatedBy(userDto.getId());
    todo.setCreated_at(LocalDateTime.now());
    Set<User> users = new HashSet<>();
    users.add(modelMapper.map(userDto, User.class));
    todo.setUsers(users);
    Todo newTodo = todoRepo.save(todo);
    return newTodo;
  }

  @Override
  public List<TodoDto> getTodos() {
    UserDto userDto = userService.getCurrentUser();
    User user = userRepo.findByEmail(userDto.getEmail());
    Set<Todo> todoSet = user.getTodos();
    List<TodoDto> todoDtoList = new ArrayList<>();
    for (Todo todo : todoSet) {
      TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
      todoDtoList.add(todoDto);
    }
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
    Set<ItemDto> itemDtoSet = new HashSet<>();
    for (Item item : itemSet) {
      itemDtoSet.add(modelMapper.map(item, ItemDto.class));
    }
    TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
    todoDto.setUsers(userDtoSet);
    todoDto.setItems(itemDtoSet);
    return todoDto;
  }

  @Override
  public List getTodoItems(Long todo_id) {
    Todo todo = todoRepo.getOne(todo_id);
    List<ItemDto> itemDtos = new ArrayList();
    Set<Item> items = todo.getItems();
    for (Item item : items) {
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

}
