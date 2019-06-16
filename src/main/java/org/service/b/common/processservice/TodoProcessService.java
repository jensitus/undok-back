package org.service.b.common.processservice;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

@Named("todoProcessService")
public class TodoProcessService {

  private static final Logger logger = LoggerFactory.getLogger(TodoProcessService.class);

  @Autowired
  private RuntimeService runtimeService;

  public void testTheProcessService() {
    logger.info("The Process Service is working well God Damn Hell Yeah!");
  }

  public void checkIfItemsOpen(Execution execution) {
    runtimeService.setVariable(execution.getId(), "itemsOpen", true);
  }

  public void sendSubItemDone() {

  }

}
