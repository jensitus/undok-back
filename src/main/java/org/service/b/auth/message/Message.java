package org.service.b.auth.message;

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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Boolean getRedirect() {
    return redirect;
  }

  public void setRedirect(Boolean redirect) {
    this.redirect = redirect;
  }

  @Override
  public String toString() {
    return "Message{" +
            "text='" + text + '\'' +
            ", trueOrFalse=" + redirect +
            '}';
  }
}
