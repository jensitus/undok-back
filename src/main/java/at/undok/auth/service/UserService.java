package at.undok.auth.service;

import at.undok.auth.model.dto.ChangePwDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.message.Message;

import java.util.List;
import java.util.UUID;

public interface UserService {

  Message createPasswordResetTokenForUser(String email);

  boolean checkIfTokenExpired(String base64Token, String encodedEmail, String confirm);

  Message resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email);

  List<UserDto> getAll();

  UserDto getCurrentUser();

  UserDto getById(UUID user_id);

  Message changePw(ChangePwDto changePwDto);

  void setAdmin(UUID userId, boolean admin);

  UserDto getByUsername(String username);

  boolean checkIfPasswordHasToBeChanged(String encodedToken, String encodedEmail);

}
