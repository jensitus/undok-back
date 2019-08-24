package org.service.b.todo.dto;

public class DescriptionDto {

  private Long id;
  private String text;
  private Long item_id;
  private Long user_id;
  private Long todo_id;

  public DescriptionDto() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getItem_id() {
    return item_id;
  }

  public void setItem_id(Long item_id) {
    this.item_id = item_id;
  }

  public Long getUser_id() {
    return user_id;
  }

  public void setUser_id(Long user_id) {
    this.user_id = user_id;
  }

  public Long getTodo_id() {
    return todo_id;
  }

  public void setTodo_id(Long todo_id) {
    this.todo_id = todo_id;
  }

  @Override
  public String toString() {
    return "DescriptionDto{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", item_id=" + item_id +
            ", user_id=" + user_id +
            '}';
  }
}
