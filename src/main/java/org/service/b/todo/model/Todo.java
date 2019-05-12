package org.service.b.todo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.service.b.auth.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "todos")
public class Todo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "title")
  @NotNull
  private String title;

  @Column(name = "created_by")
  @NotNull
  private Long createdBy;

  @Column(name = "created_at")
  private LocalDateTime created_at;

  @Column(name = "updated_at")
  private LocalDateTime updated_at;

  @Column(name = "done")
  private boolean done;

  @OneToMany
  @JoinColumn(name = "todo_id")
  private Set<Item> items;

  @ManyToMany(mappedBy = "todos")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  private Set<User> users;

  public Todo() {}

  public Todo(@NotNull String title) {
    this.title = title;
  }

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

  public LocalDateTime getCreated_at() {
    return created_at;
  }

  public void setCreated_at(LocalDateTime created_at) {
    this.created_at = created_at;
  }

  public LocalDateTime getUpdated_at() {
    return updated_at;
  }

  public void setUpdated_at(LocalDateTime updated_at) {
    this.updated_at = updated_at;
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public Set<Item> getItems() {
    return items;
  }

  public void setItems(Set<Item> items) {
    this.items = items;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}
