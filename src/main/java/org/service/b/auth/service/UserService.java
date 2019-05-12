package org.service.b.auth.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;

import java.util.List;

public interface UserService {

  void createPasswordResetTokenForUser(User user);

  boolean checkResetToken(String base64Token, String email);

  List<UserDto> getAll();

  UserDto getCurrentUser();
}
