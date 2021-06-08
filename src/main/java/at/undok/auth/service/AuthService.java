package at.undok.auth.service;

import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.message.Message;

public interface AuthService {

  UserDto getUserDtoWithJwt(LoginDto loginDto);

  UserDto createUserAfterSignUp(SignUpDto signUpDto);

  Message confirmAccount(ConfirmAccountForm confirmAccountForm);

  String createUserViaAdmin(CreateUserForm createUserForm);

  String createConfirmationUrl(String email, String confirmationToken);

  void confirmAccountWithoutPWChange(String encodedToken, String encodedEmail);

}
