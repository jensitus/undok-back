package at.undok.auth.service;

import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;

public interface AuthService {

  UserDto getUserDtoWithSecondFactorJwt(LoginDto loginDto);

  UserDto getUserDtoWithRealJwt(SecondFactorForm secondFactorForm);

  UserDto createUserAfterSignUp(SignUpDto signUpDto);

  Message confirmAccount(ConfirmAccountForm confirmAccountForm);

  String createUserViaAdmin(CreateUserForm createUserForm);

  void confirmAccountWithoutPWChange(String encodedToken, String encodedEmail);

  String resendConfirmationToken(String userId);

}
