package org.service.b.auth.serviceimpl;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.LoginForm;
import org.service.b.auth.message.SignUpForm;
import org.service.b.auth.model.Role;
import org.service.b.auth.model.RoleName;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.RoleRepo;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.security.JwtProvider;
import org.service.b.auth.service.AuthService;
import org.modelmapper.ModelMapper;
import org.service.b.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  JwtProvider jwtProvider;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  RoleRepo roleRepo;

  @Override
  public UserDto getUserDtoWithJwt(LoginForm loginForm) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtProvider.generateJwtToken(authentication);
    User user = userRepo.findByUsername(loginForm.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with -> username or email: " + loginForm.getUsername()));
    UserDto userDto = modelMapper.map(user, UserDto.class);
    userDto.setAccessToken(jwt);
    return userDto;
  }

  @Override
  public Message createUser(SignUpForm signUpForm) {
    // create the new user:
    User user = new User(signUpForm.getUsername(), signUpForm.getEmail().toLowerCase(), encoder.encode(signUpForm.getPassword()));
    Role uRole = new Role(RoleName.ROLE_USER);
    Set<String> roleSet = new HashSet<>();
    roleSet.add(uRole.getName().toString());
    signUpForm.setRole(roleSet);
    Set<String> strRoles = signUpForm.getRole();
    Set<Role> roles = new HashSet<>();
    strRoles.forEach(role -> {
      switch (role) {
        case "admin":
          Role adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("The Fucking Role couldn't be found, sorry"));
          roles.add(adminRole);
          break;
        default:
          Role userRole = roleRepo.findByName(RoleName.ROLE_USER).orElseThrow(() -> new RuntimeException("No Role Today my Love is gone away"));
          roles.add(userRole);
      }
    });
    user.setRoles(roles);
    userRepo.save(user);
    return new Message("user created");
  }
}
