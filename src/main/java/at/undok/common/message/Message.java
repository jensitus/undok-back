package at.undok.common.message;

import lombok.Data;

@Data
public class Message {

  private String text;

  private Boolean redirect;

  public Message() {
  }

  public Message(String text) {
    this.text = text;
  }

  public Message(Boolean redirect) {
    this.redirect = redirect;
  }

  public Message(String text, Boolean redirect) {
    this.text = text;
    this.redirect = redirect;
  }

}
