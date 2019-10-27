package org.service.b.common.processservice;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.batch.history.HistoricBatch;
import org.camunda.bpm.engine.batch.history.HistoricBatchQuery;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CleaningUpService {

  private static final Logger logger = LoggerFactory.getLogger(CleaningUpService.class);

  @Autowired
  private HistoryService historyService;

  public void deleteProcessInstance(String processInstanceId) {
    historyService.deleteHistoricProcessInstance(processInstanceId);
  }

  /**
   * this is for deleting old processInstances to avoid data garbage
   */

  // @Scheduled(cron = "10 * * * * * ")
  public void showTheProcesses() {
    logger.info("show the processes");
    List<HistoricProcessInstance> hbq = historyService.createHistoricProcessInstanceQuery().completed().list();
    for (HistoricProcessInstance hb : hbq) {
      logger.info(hb.getRootProcessInstanceId());
      LocalDateTime endTimeLocalDateTime = hb.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      logger.info(endTimeLocalDateTime.getClass().toString());
      logger.info(endTimeLocalDateTime.toString());
    }
    // logger.info();
  }

}
