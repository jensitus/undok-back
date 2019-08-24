package org.service.b.common.message.service;

import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.List;

public interface ServiceBProcessService {

  List<ProcessInstance> getProcessInstancesByBusinessKey(String businessKey);

  ProcessInstance getProcessInstanceByTask(String taskId);

  void setVariable(String executionId, String variableName, Boolean value);

}
