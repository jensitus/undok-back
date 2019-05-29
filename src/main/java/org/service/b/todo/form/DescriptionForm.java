package org.service.b.todo.form;

public class DescriptionForm {

  private Long id;
  private String text;

  public DescriptionForm() {
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

  @Override
  public String toString() {
    return "DescriptionForm{" +
            "id=" + id +
            ", text='" + text + '\'' +
            '}';
  }
}
