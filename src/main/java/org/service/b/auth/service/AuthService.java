package org.service.b.auth.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.LoginForm;
import org.service.b.auth.message.SignUpForm;
import org.service.b.common.message.Message;

public interface AuthService {

  UserDto getUserDtoWithJwt(LoginForm loginForm);

  Message createUser(SignUpForm signUpForm);

}
