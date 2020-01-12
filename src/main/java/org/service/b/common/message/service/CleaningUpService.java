package org.service.b.common.message.service;

public interface CleaningUpService {

  void deleteProcessInstance(String processInstanceId);

  void collectOldProcesses();

}
