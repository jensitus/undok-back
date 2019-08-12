package org.service.b.auth.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.PasswordResetForm;
import org.service.b.auth.model.User;
import org.service.b.common.message.Message;

import java.util.List;

public interface UserService {

  Message createPasswordResetTokenForUser(String email);

  boolean checkResetToken(String base64Token, String email);

  Message resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email);

  List<UserDto> getAll();

  UserDto getCurrentUser();

  UserDto getById(Long user_id);

}
