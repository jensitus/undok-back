package org.service.b.common.message.service;

public interface MigrationService {

  void migrateProcessInstance(String sourceVersion, String targetVersion, String sourceAct, String targetAct, String processInstanceId);

}
