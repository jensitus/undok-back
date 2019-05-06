package org.service.b.todo.dto;

import org.service.b.todo.model.Todo;

public class ItemDto {

  private Long id;

  private String name;

  private boolean done;

  private Todo todo;

  public ItemDto() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public Todo getTodo() {
    return todo;
  }

  public void setTodo(Todo todo) {
    this.todo = todo;
  }
}
