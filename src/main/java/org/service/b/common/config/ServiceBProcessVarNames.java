package org.service.b.common.config;

public enum ServiceBProcessVarNames {

  TODO_PROCESS_DEFINITION_KEY("service-b-todo"),
  ENTITY_ID("entityId");

  public final String value;

  private ServiceBProcessVarNames( String value) {
    this.value = value;
  }

}
