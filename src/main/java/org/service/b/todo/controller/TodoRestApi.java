package org.service.b.todo.controller;

import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.form.TodoForm;
import org.service.b.todo.model.Todo;
import org.service.b.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/todos")
public class TodoRestApi {

  private static final Logger logger = LoggerFactory.getLogger(TodoRestApi.class);

  @Autowired
  private TodoService todoService;

  @GetMapping("/")
  public ResponseEntity<Set> getTodos() {
    List<TodoDto> todos = todoService.getTodos();
    return new ResponseEntity(todos, HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity createTodo(@RequestBody TodoForm todoForm) {
    logger.info("Todo Title: " + todoForm.getTitle());
    Todo todo = todoService.createTodo(todoForm.getTitle());
    return new ResponseEntity(todo, HttpStatus.OK);
  }

}
