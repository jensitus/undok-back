package org.service.b.todo.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ItemDto {

  @NotNull
  private Long id;

  private String name;

  private boolean done;

  private Long todoId;

  private LocalDate dueDate;

  private Long createdBy;

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

  public Long getTodoId() {
    return todoId;
  }

  public void setTodoId(Long todoId) {
    this.todoId = todoId;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public String toString() {
    return "ItemDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", done=" + done +
            ", todo_id=" + todoId +
            ", dueDate=" + dueDate +
            '}';
  }
}
