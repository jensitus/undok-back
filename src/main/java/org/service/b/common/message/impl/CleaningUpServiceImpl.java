package org.service.b.common.message.impl;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.service.b.common.message.service.CleaningUpService;
import org.service.b.todo.repository.TodoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CleaningUpServiceImpl implements CleaningUpService {

  private static final Logger logger = LoggerFactory.getLogger(CleaningUpServiceImpl.class);

  private static final String SUB_TODO_SERVICE_ITEM = "sub-todo-service-item";

  private static final String SERVICE_B_TODO = "service-b-todo";

  private static final String CLEANING_UP = "cleaning-up-process";

  @Autowired
  private HistoryService historyService;

  @Autowired
  private TodoRepo todoRepo;

  /**
   * this is for deleting old processInstances to avoid data garbage
   */

  @Override
  public void collectOldProcesses() {
    logger.info("we try real hard to find old processes");
    List<HistoricProcessInstance> hbq = historyService.createHistoricProcessInstanceQuery().completed().list();
    for (HistoricProcessInstance hb : hbq) {
      LocalDateTime endTimeLocalDateTime = hb.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      Duration duration = Duration.between(endTimeLocalDateTime, LocalDateTime.now());
      if (duration.toDays() > 30) {
        if (SERVICE_B_TODO.equals(hb.getProcessDefinitionKey()) || CLEANING_UP.equals(hb.getProcessDefinitionKey())) {
          deleteProcessInstance(hb.getRootProcessInstanceId());
          logger.info("process deleted: " + hb.getProcessDefinitionKey());
        }
      }
    }
    List<HistoricProcessInstance> extHbq = historyService.createHistoricProcessInstanceQuery().externallyTerminated().list();
    for (HistoricProcessInstance h : extHbq) {
      LocalDateTime endLocalDateTime = h.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      Duration duration = Duration.between(endLocalDateTime, LocalDateTime.now());
      if (duration.toDays() > 30) {
        if (h.getProcessDefinitionKey().equals(SERVICE_B_TODO)) {
          deleteProcessInstance(h.getRootProcessInstanceId());
        }
      }
    }
  }

  @Override
  public void deleteProcessInstance(String processInstanceId) {
    historyService.deleteHistoricProcessInstance(processInstanceId);
  }

}
