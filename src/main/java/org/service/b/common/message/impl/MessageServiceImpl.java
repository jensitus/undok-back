package org.service.b.common.message.impl;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.service.b.common.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

  private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

  @Autowired
  private RuntimeService runtimeService;

  @Override
  public void sendMessageToCatchEvent(String messageName, String processDefinitionKey, Long entityId) {
    List<ProcessInstance> pis = getProcessInstanceList(processDefinitionKey, entityId);
    for (ProcessInstance pi : pis) {
      List<EventSubscription> eventSubscriptionList = runtimeService.createEventSubscriptionQuery().processInstanceId(pi.getProcessInstanceId()).eventName(messageName).list();
      if (eventSubscriptionList.size() > 1 ) {
        throw new IllegalStateException("We are sorry, but there is more then one subcription. " + eventSubscriptionList.size() + " to be precise");
      } else if ( eventSubscriptionList.size() == 1) {
        runtimeService.messageEventReceived(messageName, eventSubscriptionList.get(0).getExecutionId());
      }
      logger.info("message " + messageName + " to " + processDefinitionKey + " successfully sent");
    }
  }

  private List<ProcessInstance> getProcessInstanceList(String processDefinitionKey, Long entityId) {
    List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery()
            .processDefinitionKey(processDefinitionKey)
            .variableValueEquals("entityId", entityId)
            .list();
    return pis;
  }

}
