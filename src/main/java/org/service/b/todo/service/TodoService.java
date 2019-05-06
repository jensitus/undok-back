package org.service.b.todo.service;

import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.model.Todo;

import java.util.List;
import java.util.Set;

public interface TodoService {

  Todo createTodo(String title);

  List<TodoDto> getTodos();

}
