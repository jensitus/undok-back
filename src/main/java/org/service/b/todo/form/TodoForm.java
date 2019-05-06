package org.service.b.todo.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TodoForm {

  @NotBlank
  @Size(min = 9)
  private String title;

  public TodoForm() {  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
