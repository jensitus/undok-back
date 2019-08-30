package org.service.b.common.config;

public enum ServiceBProcessEnums {

  TODO_PROCESS_DEFINITION_KEY("service-b-todo"),
  ENTITY_ID("entityId"),
  TODO_SIMPLE("simple"),
  SERVICE_B_EMAIL_SUBJECT_PREFIX ("[service-b.org]"),
  SERVICE_B_BASE_URL("https://service-b.org/"),
  TODO_GROUP_PREFIX("todo");

  public final String value;

  private ServiceBProcessEnums(String value) {
    this.value = value;
  }

}
