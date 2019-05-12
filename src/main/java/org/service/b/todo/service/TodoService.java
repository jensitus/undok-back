package org.service.b.todo.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.todo.dto.ItemDto;
import org.service.b.todo.dto.TodoDto;
import org.service.b.todo.model.Todo;

import java.util.List;
import java.util.Set;

public interface TodoService {

  Todo createTodo(String title);

  List<TodoDto> getTodos();

  TodoDto getTodoById(Long todo_id);

  List<ItemDto> getTodoItems(Long todo_id);

  List<UserDto> getTodoUsers(Long todo_id);

  TodoDto addUserToTodo(Long todo_id, Long user_id);

}
