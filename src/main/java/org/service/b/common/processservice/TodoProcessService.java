package org.service.b.common.processservice;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.service.b.common.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named("todoProcessService")
public class TodoProcessService {

  private static final Logger logger = LoggerFactory.getLogger(TodoProcessService.class);

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private MessageService messageService;

  public void startTodo(Long todo_id) {
    Map variables = new HashMap();
    variables.put("entityId", todo_id);
    ProcessInstance todoProcessInstance = runtimeService.startProcessInstanceByKey("service-b-todo", "service-b-todo-" + todo_id.toString(), variables);
  }

  public void testTheProcessService() {
    logger.info("The Process Service is working well God Damn Hell Yeah!");
  }

  public void startSubTodoServiceItem(Long todo_id, Long item_id) {
    Map variables = new HashMap();
    variables.put("entityId", item_id);
    runtimeService.startProcessInstanceByKey("sub-todo-service-item", "service-b-todo-" + todo_id.toString(), variables);

  }

  public void checkIfItemsOpen(Execution execution) {
    runtimeService.setVariable(execution.getId(), "itemsOpen", true);
  }

  public void sendSubItemDone(String messageName, String processDefinitionKey, Long entityId) {
    messageService.sendMessageToCatchEvent(messageName, processDefinitionKey, entityId);
  }

}
