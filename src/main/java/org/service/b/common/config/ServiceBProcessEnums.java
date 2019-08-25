package org.service.b.common.config;

public enum ServiceBProcessEnums {

  TODO_PROCESS_DEFINITION_KEY("service-b-todo"),
  ENTITY_ID("entityId"),
  TODO_SIMPLE("simple");

  public final String value;

  private ServiceBProcessEnums(String value) {
    this.value = value;
  }

}
