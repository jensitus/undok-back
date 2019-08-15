package org.service.b.common.message.impl;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.service.b.common.message.service.ServiceBProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceBProcessServiceImpl implements ServiceBProcessService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceBProcessServiceImpl.class);

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private TaskService taskService;

  @Override
  public List<ProcessInstance> getProcessInstancesByBusinessKey(String businessKey) {
    List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).active().list();
    return piList;
  }

  @Override
  public ProcessInstance getProcessInstanceByTask(String taskId) {
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
    logger.info("process Instance " + pi.toString());
    logger.info(pi.getProcessDefinitionId() + " " + pi.getBusinessKey() + " " + pi.getProcessInstanceId());
    return pi;
  }

  @Override
  public void setVariable(String executionId, String variableName, Boolean value) {
    logger.info(executionId);
    logger.info(variableName);
    logger.info(value.toString());
    runtimeService.setVariable(executionId, variableName, value);
  }
}
