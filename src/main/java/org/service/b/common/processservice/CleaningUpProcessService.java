package org.service.b.common.processservice;

import org.service.b.common.message.service.CleaningUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;

@Named("cleaningUp")
public class CleaningUpProcessService {

  private static final Logger logger = LoggerFactory.getLogger(CleaningUpProcessService.class);

  @Autowired
  private CleaningUpService cleaningUpService;

  public void cleaningUpOldProcesses() {
    logger.info("donner wetter, we  are doing the cleaning up");
    cleaningUpService.collectOldProcesses();
  }

}
