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

import javax.ws.rs.Path;

@CrossOrigin(origins = "*", maxAge = 3600)
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

}
