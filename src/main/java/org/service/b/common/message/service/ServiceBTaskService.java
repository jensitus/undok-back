package org.service.b.common.message.service;


import org.camunda.bpm.engine.task.Task;
import org.service.b.common.dto.TaskDto;

import java.util.List;

public interface ServiceBTaskService {

  String getTaskFormKey(String processDefinitionId, String taskDefinitionKey);

  TaskDto getSingleTask(String task_id);

  List<TaskDto> taskList(String user_id);

}
