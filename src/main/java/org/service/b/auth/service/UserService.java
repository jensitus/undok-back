package org.service.b.auth.service;

import org.service.b.auth.model.User;

public interface UserService {

  void createPasswordResetTokenForUser(User user);

  boolean checkResetToken(String base64Token, String email);
}
