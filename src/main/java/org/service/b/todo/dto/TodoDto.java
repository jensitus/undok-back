package org.service.b.todo.dto;

import org.service.b.auth.dto.UserDto;

import java.util.List;
import java.util.Set;

public class TodoDto {

  private Long id;

  private String title;

  private Long createdBy;

  private boolean done;

  private List<ItemDto> items;

  private Set<UserDto> users;

  public TodoDto() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public List<ItemDto> getItems() {
    return items;
  }

  public void setItems(List<ItemDto> items) {
    this.items = items;
  }

  public Set<UserDto> getUsers() {
    return users;
  }

  public void setUsers(Set<UserDto> users) {
    this.users = users;
  }
}
