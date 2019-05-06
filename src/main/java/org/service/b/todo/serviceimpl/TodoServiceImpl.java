package org.service.b.todo.serviceimpl;

import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.serviceimpl.UserPrinciple;
import org.service.b.todo.dto.TodoDto;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TodoServiceImpl implements TodoService {

  private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

  @Autowired
  private TodoRepo todoRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepo userRepo;

  @Override
  public Todo createTodo(String title) {
    Todo todo = new Todo(title);
    UserDto userDto = getCurrentUser();
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
    UserDto userDto = getCurrentUser();
    User user = userRepo.findByEmail(userDto.getEmail());
    logger.info(user.getEmail());
    logger.info(user.getTodos().toString());
    Set<Todo> todoSet = user.getTodos();
    List<TodoDto> todoDtoList = new ArrayList<>();
    for (Todo todo : todoSet) {
      logger.info(todo.toString());
      logger.info(todo.getTitle());
      TodoDto todoDto = modelMapper.map(todo, TodoDto.class);
      todoDtoList.add(todoDto);
    }
    return todoDtoList;
  }

  private UserDto getCurrentUser() {
    UserDto userDto;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserPrinciple userPrinciple = (UserPrinciple) auth.getPrincipal();
    userDto = modelMapper.map(userPrinciple, UserDto.class);
    return userDto;
  }

}
