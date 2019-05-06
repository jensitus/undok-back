package org.service.b.auth.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.LoginForm;

public interface AuthService {

  UserDto getUserDtoWithJwt(LoginForm loginForm);

}
