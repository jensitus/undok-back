package org.service.b.common.message.service;

public interface ServiceBCamundaUserService {

  void addNewUserToCamunda(String id, String email, String username);

  void addUserToCamundaGroup(String user_id, Long entity_id, String groupPrefix);

  String getTheCamundaGroupId(String groupPrefix, Long entityId);

}
