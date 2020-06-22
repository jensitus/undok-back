package org.service.b.common.message.service;


import org.service.b.common.dto.TaskDto;

import java.util.List;

public interface ServiceBTaskService {

  String getTaskFormKey(String processDefinitionId, String taskDefinitionKey);

  TaskDto getSingleTask(String task_id);

  List<TaskDto> taskList(String user_id);

  void completeTask(String task_id);

  String getVariable(String execution_id, String variableName);

  String getTaskForTimeLine(Long todo_id);

  String getTaskIdByLongVariable(String variableName, Long longVariable);

}
