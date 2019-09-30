package org.service.b.todo.controller;

import org.service.b.auth.message.UserForm;
import org.service.b.common.message.Message;
import org.service.b.common.message.service.MessageService;
import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.form.TodoForm;
import org.service.b.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"})
@RestController
@RequestMapping("/service/todos")
public class TodoRestApi {

  private static final Logger logger = LoggerFactory.getLogger(TodoRestApi.class);

  @Autowired
  private TodoService todoService;

  @Autowired
  private MessageService messageService;

  @GetMapping("/")
  public ResponseEntity getTodos() {
    List<TodoDto> todos = todoService.getTodos();
    return new ResponseEntity(todos, HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity createTodo(@RequestBody TodoForm todoForm) {
    logger.info("Todo Title: " + todoForm.getTitle());
    TodoDto todoDto = todoService.createTodo(todoForm.getTitle());
    return new ResponseEntity(todoDto, HttpStatus.OK);
  }

  @GetMapping("/{todo_id}")
  public ResponseEntity getTodo(@PathVariable("todo_id") Long todo_id) {
    TodoDto todoDto = todoService.getTodoById(todo_id);
    return new ResponseEntity(todoDto, HttpStatus.OK);
  }

  @GetMapping("/{todo_id}/users")
  public ResponseEntity getTodoUsers(@PathVariable("todo_id") Long todo_id) {
    return new ResponseEntity(todoService.getTodoUsers(todo_id), HttpStatus.OK);
  }

  @PostMapping("/{todo_id}/add_user")
  public ResponseEntity addUserToTodo(@PathVariable("todo_id") Long todo_id, @RequestBody UserForm userForm) {
    logger.info("todo_id: " + todo_id + " userForm.getId(): " + userForm.getUserId());
    TodoDto todoDto = todoService.addUserToTodo(todo_id, userForm.getUserId());
    return new ResponseEntity(todoDto, HttpStatus.OK);
  }

  @PutMapping("/{todo_id}")
  public ResponseEntity updateTodo(@PathVariable("todo_id") Long todo_id) {
    TodoDto todoDto = todoService.updateTodo(todo_id);
    return new ResponseEntity(todoDto, HttpStatus.OK);
  }

  @DeleteMapping("/{todo_id}")
  public ResponseEntity deleteTodo(@PathVariable("todo_id") Long todo_id) {
    Message message = todoService.todoFinished(todo_id);
    logger.info("delete Todo message: {}", message.toString());
    if (message.getTrueOrFalse()) {
      return new ResponseEntity(message, HttpStatus.OK);
    } else {
      return new ResponseEntity(message, HttpStatus.OK);
    }
  }

}
