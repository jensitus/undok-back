package at.undok.auth.controller;

import at.undok.auth.model.dto.ChangePwDto;
import at.undok.auth.model.dto.SetAdminDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.security.JwtProvider;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.auth.serviceimpl.UserDetailsServiceImpl;
import at.undok.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RestController
@RequestMapping("/service/users")
public class UserRestApi {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  JwtProvider jwtProvider;

  @Autowired
  private AuthService authService;

  @GetMapping("/all")
  public List<UserDto> getAllUsers() {
    List<UserDto> userDtoList = userService.getAll();
    return userDtoList;
  }

  @GetMapping("/principle/{username}")
  public UserDetails getSpecialUser(@PathVariable String username) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return userDetails;
  }

  @GetMapping("/by_username/{username}")
  public UserDto getUserByUsername(@PathVariable("username") String username) {
    return userService.getByUsername(username);
  }

  @PostMapping("/auth/check_auth_token")
  public ResponseEntity<Message> checkTheAuthToken(@RequestBody String token) {
    Message message = jwtProvider.validateJwtToken(token);
    if (jwtProvider.validateJwtToken(token).getRedirect()) {
      return new ResponseEntity<>(message, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/auth/password_resets")
  public ResponseEntity<Message> password_resets(@RequestBody String email) {
    userService.createPasswordResetTokenForUser(email);
    return new ResponseEntity<>(new Message("jess god damn"), HttpStatus.OK);
  }

  @PostMapping("/changepw")
  public ResponseEntity changePw(@Valid @RequestBody ChangePwDto changePwDto) {
    Message m = userService.changePw(changePwDto);
    if (m.getRedirect()) {
      return new ResponseEntity<>(new Message(m.getText()), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(m, HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @PostMapping("/set-admin/{user_id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Message setAdminFlag(@PathVariable("user_id") UUID userId, @RequestBody SetAdminDto setAdminDto) {
    userService.setAdmin(userId, setAdminDto.isAdmin());
    return new Message("successfully changed");
  }

  @PostMapping("/create-user-via-admin")
  public ResponseEntity createUserViaAdmin(@RequestBody CreateUserForm createUserForm) {
    String confirmationUrl = authService.createUserViaAdmin(createUserForm);
    return new ResponseEntity(new Message(confirmationUrl), HttpStatus.OK);
  }

}
