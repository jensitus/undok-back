package org.service.b.common.message.service;


import org.service.b.common.dto.TaskDto;

public interface ServiceBTaskService {

  String getTaskFormKey(String processDefinitionId, String taskDefinitionKey);

  TaskDto getSingleTask(String task_id);

}
