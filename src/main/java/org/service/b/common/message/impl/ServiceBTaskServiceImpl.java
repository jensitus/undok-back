package org.service.b.common.message.impl;

import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.modelmapper.ModelMapper;
import org.service.b.common.config.ServiceBProcessEnum;
import org.service.b.common.dto.TaskDto;
import org.service.b.common.message.service.ServiceBTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceBTaskServiceImpl implements ServiceBTaskService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceBTaskServiceImpl.class);

  @Autowired
  private FormService formService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  public String getTaskFormKey(String processDefinitionId, String taskDefinitionKey) {
    String formKey = formService.getTaskFormKey(processDefinitionId, taskDefinitionKey);
    return formKey;
  }

  @Override
  public TaskDto getSingleTask(String task_id) {
    Task task = taskService.createTaskQuery().initializeFormKeys().taskId(task_id).singleResult();
    if (task == null) {
      return null;
    } else {
      return mapTaskToDto(task);
    }
  }

  @Override
  public String getTaskForTimeLine(Long todo_id) {
    String businessKey = ServiceBProcessEnum.TODO_PROCESS_DEFINITION_KEY.getValue() + "-" + todo_id.toString();
    List<Task> taskList = new ArrayList<>();
    List<ProcessInstance> piList = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(businessKey).active().list();
    for (ProcessInstance pi : piList) {
       for (Task task : taskService.createTaskQuery().active().processInstanceId(pi.getProcessInstanceId()).list()) {
         taskList.add(task);
       }
    }
    if (taskList.size() == 1) {
      return taskList.get(0).getId();
    } else if (taskList.size() > 1) {
      return taskList.get(0).getId();
    } else {
      return null;
    }
  }

  @Override
  public List<TaskDto> taskList(String user_id) {
    List<Task> candidateTasks = taskService.createTaskQuery().active().taskCandidateUser(user_id).list();
    List<Task> assigneeTasks = taskService.createTaskQuery().active().taskAssignee(user_id).list();
    List<TaskDto> taskDtoList = new ArrayList<>();
    for (Task t : candidateTasks) {
      Task task = taskService.createTaskQuery().initializeFormKeys().taskId(t.getId()).singleResult();
      taskDtoList.add(mapTaskToDto(task));
    }
    for (Task t : assigneeTasks) {
      Task task = taskService.createTaskQuery().initializeFormKeys().taskId(t.getId()).singleResult();
      taskDtoList.add(mapTaskToDto(task));
    }
    return taskDtoList;
  }

  @Override
  public void completeTask(String task_id) {
    taskService.complete(task_id);
  }

  @Override
  public String getVariable(String execution_id, String variableName) {
    Object entityId = runtimeService.getVariable(execution_id, variableName);
    String classNameDonnerWetter = entityId.getClass().toString();
    return entityId.toString();
  }

  private TaskDto mapTaskToDto(Task t) {
    TaskDto taskDto = new TaskDto();
    taskDto.setId(t.getId());
    taskDto.setAssignee(t.getAssignee());
    taskDto.setCaseDefinitionId(t.getCaseDefinitionId());
    taskDto.setCaseInstanceId(t.getCaseInstanceId());
    taskDto.setCaseExecutionId(t.getCaseExecutionId());
    taskDto.setDelegationState(t.getDelegationState());
    taskDto.setDescription(t.getDescription());
    taskDto.setExecutionId(t.getExecutionId());
    taskDto.setFormKey(t.getFormKey());
    taskDto.setName(t.getName());
    taskDto.setOwner(t.getOwner());
    taskDto.setParentTaskId(t.getParentTaskId());
    taskDto.setPriority(t.getPriority());
    taskDto.setProcessDefinitionId(t.getProcessDefinitionId());
    taskDto.setProcessInstanceId(t.getProcessInstanceId());
    taskDto.setTenantId(t.getTenantId());
    taskDto.setTaskDefinitionKey(t.getTaskDefinitionKey());
    taskDto.setPriority(t.getPriority());
    if (t.getCreateTime() != null) {
      taskDto.setCreateTime(t.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }
    if (t.getDueDate() != null) {
      taskDto.setDueDate(t.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    if (t.getFollowUpDate() != null) {
      taskDto.setFollowUpDate(t.getFollowUpDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    return taskDto;
  }

}
