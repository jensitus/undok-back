package org.service.b.auth.service;

import org.service.b.auth.model.dto.ConfirmAccountDto;
import org.service.b.auth.model.dto.UserDto;
import org.service.b.auth.model.dto.LoginDto;
import org.service.b.auth.model.dto.SignUpDto;
import org.service.b.auth.message.Message;
import org.service.b.auth.model.form.CreateUserForm;

public interface AuthService {

  UserDto getUserDtoWithJwt(LoginDto loginDto);

  UserDto createUserAfterSignUp(SignUpDto signUpDto);

  Message confirmAccount(ConfirmAccountDto confirmAccountDto);

  void createUserViaAdmin(CreateUserForm createUserForm);

}
