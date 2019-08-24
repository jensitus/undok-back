package org.service.b.common.message.service;

public interface MessageService {

  void sendMessageToCatchEvent(String messageName, String processDefinitionKey, Long entityId);

}
