package org.service.b.auth.service;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.dto.LoginDto;
import org.service.b.auth.dto.SignUpDto;
import org.service.b.auth.message.Message;

public interface AuthService {

  UserDto getUserDtoWithJwt(LoginDto loginDto);

  UserDto createUser(SignUpDto signUpDto);

  Message confirmAccount(String token, String email);

}
