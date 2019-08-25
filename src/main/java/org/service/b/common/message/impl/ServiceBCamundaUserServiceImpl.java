package org.service.b.common.message.impl;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.User;
import org.service.b.common.message.service.ServiceBCamundaUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceBCamundaUserServiceImpl implements ServiceBCamundaUserService {

  private static final Logger logger = LoggerFactory.getLogger(ServiceBCamundaUserServiceImpl.class);

  final static String[] ALPHABET = {"O", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

  @Autowired
  private IdentityService identityService;

  @Override
  public void addNewUserToCamunda(String id, String email, String username) {
    User camundaUser = identityService.newUser(id);
    camundaUser.setEmail(email);
    camundaUser.setFirstName(username);
    identityService.saveUser(camundaUser);
  }

  @Override
  public void addUserToCamundaGroup(String user_id, Long entity_id, String groupPrefix) {
    String groupId = theGroupId(groupPrefix, entity_id);
    logger.info(groupId);
    logger.info(user_id);
    identityService.createMembership(user_id, groupId);
  }

  @Override
  public String getTheCamundaGroupId(String groupPrefix, Long entityId) {
    return theGroupId(groupPrefix, entityId);
  }

  private String theGroupId(String groupPrefix, Long entityId) {
    String todoId = entityId.toString();
    String theFinalGroupId = groupPrefix;
    for (int i = 0; i < todoId.length(); i++) {
      logger.info(ALPHABET[Integer.parseInt(String.valueOf(todoId.charAt(i)))]);
      theFinalGroupId = theFinalGroupId + ALPHABET[Integer.parseInt(String.valueOf(todoId.charAt(i)))];
    }
    return theFinalGroupId;
  }
}
