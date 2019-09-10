package org.service.b.todo.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class ItemDto {

  @NotNull
  private Long id;

  private String name;

  private boolean done;

  private Long todo_id;

  private LocalDate dueDate;

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

  public Long getTodo_id() {
    return todo_id;
  }

  public void setTodo_id(Long todo_id) {
    this.todo_id = todo_id;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public String toString() {
    return "ItemDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", done=" + done +
            ", todo_id=" + todo_id +
            ", dueDate=" + dueDate +
            '}';
  }
}
