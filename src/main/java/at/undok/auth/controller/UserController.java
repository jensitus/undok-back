package at.undok.auth.controller;

import at.undok.auth.api.UserApi;
import at.undok.auth.model.dto.ChangePwDto;
import at.undok.auth.model.dto.LockUserDto;
import at.undok.auth.model.dto.SetAdminDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.security.JwtProvider;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.auth.serviceimpl.UserDetailsServiceImpl;
import at.undok.common.message.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController implements UserApi {

  private final UserDetailsServiceImpl userDetailsService;

  private final UserService userService;

  final
  JwtProvider jwtProvider;

  private final AuthService authService;

  private final UserRepo userRepo;

  public UserController(UserDetailsServiceImpl userDetailsService, UserService userService, JwtProvider jwtProvider, AuthService authService, UserRepo userRepo) {
    this.userDetailsService = userDetailsService;
    this.userService = userService;
    this.jwtProvider = jwtProvider;
    this.authService = authService;
    this.userRepo = userRepo;
  }

  public List<UserDto> getAllUsers() {
    List<UserDto> userDtoList = userService.getAll();
    return userDtoList;
  }

  public UserDetails getSpecialUser(String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return userDetails;
  }

  public UserDto getUserByUsername(String username) {
    return userService.getByUsername(username);
  }

  public ResponseEntity<Message> checkTheAuthToken(String token) {
    Message message = jwtProvider.validateJwtToken(token);
    if (jwtProvider.validateJwtToken(token).getRedirect()) {
      return new ResponseEntity<>(message, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }
  }

  public ResponseEntity<Message> password_resets(String email) {
    userService.createPasswordResetTokenForUser(email);
    return new ResponseEntity<>(new Message("jess god damn"), HttpStatus.OK);
  }

  public ResponseEntity changePw(ChangePwDto changePwDto) {
    Message m = userService.changePw(changePwDto);
    if (m.getRedirect()) {
      return new ResponseEntity<>(new Message(m.getText()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(m, HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  public Message setAdminFlag(UUID userId, SetAdminDto setAdminDto) {
    userService.setAdmin(userId, setAdminDto.isAdmin());
    return new Message("successfully changed");
  }

  public ResponseEntity createUserViaAdmin(CreateUserForm createUserForm) {
    if (userRepo.existsByUsername(createUserForm.getUsername())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("Damn -> this Username is already taken"));
    }
    if (userRepo.existsByEmail(createUserForm.getEmail())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message("It's a pity -> but this Email is already in use!"));
    }
    String confirmationUrl = authService.createUserViaAdmin(createUserForm);
    return  ResponseEntity.ok(new Message(confirmationUrl));
  }

  @Override
  public ResponseEntity<Message> resendConfirmationLink(String userId) {
    String s = authService.resendConfirmationToken(userId);
    return ResponseEntity.ok(new Message("Confirmation link successfully sent"));
  }

  @Override
  public ResponseEntity<Message> lockUser(LockUserDto lockUserDto) {
    boolean locked = userService.lockUser(lockUserDto);
    Message message = new Message();
    if (locked) {
      message.setText("user locked");
    } else {
      message.setText("user unlocked");
    }
    return ResponseEntity.ok(message);
  }

}
