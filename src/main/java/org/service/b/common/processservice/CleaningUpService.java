package org.service.b.common.processservice;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
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
public class CleaningUpService {

  private static final Logger logger = LoggerFactory.getLogger(CleaningUpService.class);

  private static final String SUB_TODO_SERVICE_ITEM = "sub-todo-service-item";

  private static final String SERVICE_B_TODO = "service-b-todo";

  @Autowired
  private HistoryService historyService;

  public void deleteProcessInstance(String processInstanceId) {
    historyService.deleteHistoricProcessInstance(processInstanceId);
  }

  /**
   * this is for deleting old processInstances to avoid data garbage
   */

  @Scheduled(cron = "10 * * * * * ")
  public void showTheProcesses() {
    logger.info("show the processes");
    List<HistoricProcessInstance> hbq = historyService.createHistoricProcessInstanceQuery().completed().list();
    for (HistoricProcessInstance hb : hbq) {
      logger.info(hb.getRootProcessInstanceId());
      logger.info(hb.getProcessDefinitionKey());
      logger.info(hb.getProcessDefinitionId());
      LocalDateTime endTimeLocalDateTime = hb.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      logger.info(endTimeLocalDateTime.getClass().toString());
      logger.info(endTimeLocalDateTime.toString());
      Duration duration = Duration.between(endTimeLocalDateTime, LocalDateTime.now());
      logger.info(duration.toString());
      Long h = duration.toDays();
      logger.info(h.toString());
      if (duration.toDays() > 30) {
        if (hb.getProcessDefinitionKey().equals(SERVICE_B_TODO)) {
          deleteProcessInstance(hb.getRootProcessInstanceId());
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
    // logger.info();
  }

}
