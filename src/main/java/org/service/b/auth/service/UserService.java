package org.service.b.auth.service;

import org.service.b.auth.dto.ChangePwDto;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.PasswordResetForm;
import org.service.b.auth.message.Message;

import java.util.List;
import java.util.UUID;

public interface UserService {

  Message createPasswordResetTokenForUser(String email);

  boolean checkIfTokenExpired(String base64Token, String email, String confirm);

  Message resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email);

  List<UserDto> getAll();

  UserDto getCurrentUser();

  UserDto getById(UUID user_id);

  Message changePw(ChangePwDto changePwDto);

  void setAdmin(UUID userId, boolean admin);

}
