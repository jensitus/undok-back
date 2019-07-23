package org.service.b.common.controller;

import org.service.b.common.dto.TaskDto;
import org.service.b.common.message.Message;
import org.service.b.common.message.service.MigrationService;
import org.service.b.common.message.service.ServiceBTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/service/app")
public class CommonRestApi {

  private static final Logger logger = LoggerFactory.getLogger(CommonRestApi.class);

  @Autowired
  private MigrationService migrationService;

  @Autowired
  private ServiceBTaskService serviceBTaskService;

  @PostMapping("/migrate/{sourceVersion}/{targetVersion}/{sourceAct}/{targetAct}/{processInstanceId}")
  public void migrateProcessInstances(@PathVariable("sourceVersion") String sourceVersion,
                                      @PathVariable("targetVersion") String targetVersion,
                                      @PathVariable("sourceAct") String sourceAct,
                                      @PathVariable("targetAct") String targetAct,
                                      @PathVariable("processInstanceId") String processInstanceId) {
    logger.info(sourceVersion);
    logger.info(targetVersion);
    logger.info(sourceAct);
    logger.info(targetAct);
    logger.info(processInstanceId);
    migrationService.migrateProcessInstance(sourceVersion, targetVersion, sourceAct, targetAct, processInstanceId);
  }

  @GetMapping("/formkey/{processDefinitionId}/{taskDefinitionKey}")
  public String getTheFormKeyAlter(@PathVariable("processDefinitionId") String processDefinitionId,
                                   @PathVariable("taskDefinitionKey") String taskDefinitionKey) {
    String formKey = serviceBTaskService.getTaskFormKey(processDefinitionId, taskDefinitionKey);
    return formKey;
  }

  @GetMapping("/task/{task_id}")
  public ResponseEntity getSingleTask(@PathVariable("task_id") String task_id) {
    logger.info("get the Task");
    TaskDto taskDto = serviceBTaskService.getSingleTask(task_id);
    logger.info(taskDto.toString());
    return new ResponseEntity(taskDto, HttpStatus.OK);
  }

  @GetMapping("/task/list/{user_id}")
  public ResponseEntity getTaskList(@PathVariable("user_id") String user_id) {
    List<TaskDto> taskList = serviceBTaskService.taskList(user_id);
    return new ResponseEntity(taskList, HttpStatus.OK);
  }

  @PostMapping("/task/complete")
  public ResponseEntity completeTask(@RequestBody String task_id) {
    serviceBTaskService.completeTask(task_id);
    Message message = new Message("Task successfully completed, congrats, @lter");
    return new ResponseEntity(message, HttpStatus.OK);
  }

  @GetMapping("/{execution_id}/variable")
  public ResponseEntity getVariable(@PathVariable("execution_id") String execution_id, @RequestParam("name") String variable_name) {
    return new ResponseEntity(serviceBTaskService.getVariable(execution_id, variable_name), HttpStatus.OK);
  }

}
