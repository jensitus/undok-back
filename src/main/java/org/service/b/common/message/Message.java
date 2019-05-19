package org.service.b.common.message;

public class Message {

  private String text;

  private Boolean trueOrFalse;

  public Message() {
  }

  public Message(String text) {
    this.text = text;
  }

  public Message(Boolean trueOrFalse) {
    this.trueOrFalse = trueOrFalse;
  }

  public Message(String text, Boolean trueOrFalse) {
    this.text = text;
    this.trueOrFalse = trueOrFalse;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Boolean getTrueOrFalse() {
    return trueOrFalse;
  }

  public void setTrueOrFalse(Boolean trueOrFalse) {
    this.trueOrFalse = trueOrFalse;
  }

  @Override
  public String toString() {
    return "Message{" +
            "text='" + text + '\'' +
            ", trueOrFalse=" + trueOrFalse +
            '}';
  }
}
