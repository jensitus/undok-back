package org.service.b.common.message.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.service.b.common.message.service.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MigrationServiceImpl implements MigrationService {

  private static final Logger logger = LoggerFactory.getLogger(MigrationServiceImpl.class);

  @Autowired
  private ProcessEngine processEngine;

  @Autowired
  private RuntimeService runtimeService;

  @Override
  public void migrateProcessInstance(String sourceVersion, String targetVersion, String sourceAct, String targetAct, String processInstanceId) {
    MigrationPlan migrationPlan = processEngine.getRuntimeService()
            .createMigrationPlan(sourceVersion, targetVersion)
            .mapActivities(sourceAct, targetAct)
            .build();
    List<String> pids = new ArrayList<>();
    pids.add(processInstanceId);
    runtimeService.newMigration(migrationPlan).processInstanceIds(pids).execute();
    logger.info("Process: " + sourceVersion + " successfully migrated to " + targetVersion);
  }
}
