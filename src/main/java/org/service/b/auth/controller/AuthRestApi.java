package org.service.b.auth.controller;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.JwtResponse;
import org.service.b.auth.message.LoginForm;
import org.service.b.auth.message.PasswordResetForm;
import org.service.b.auth.message.SignUpForm;
import org.service.b.auth.model.Role;
import org.service.b.auth.model.RoleName;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.RoleRepo;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.AuthService;
import org.service.b.auth.service.UserService;
import org.service.b.auth.security.JwtProvider;
import org.service.b.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/service/auth")
public class AuthRestApi {

  private static final Logger logger = LoggerFactory.getLogger(AuthRestApi.class);

  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepo userRepo;

  @Autowired
  JwtProvider jwtProvider;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginForm loginForm) {
    UserDto userDto = authService.getUserDtoWithJwt(loginForm);
    return new ResponseEntity<>(new JwtResponse(userDto), HttpStatus.OK);
  }

  @PostMapping(value = "/signup", consumes = {})
  public ResponseEntity registerUser(@Valid @RequestBody SignUpForm signUpForm) {

    // check if user or email already present
    if (userRepo.existsByUsername(signUpForm.getUsername())) {
      return new ResponseEntity(new Message("Too Bad -> Username is already taken"), HttpStatus.BAD_REQUEST);
    }
    if (userRepo.existsByEmail(signUpForm.getEmail())) {
      return new ResponseEntity(new Message("It's a pity -> but this Email is already in use!"), HttpStatus.BAD_REQUEST);
    }
    if (!signUpForm.getPassword_confirmation().equals(signUpForm.getPassword())) {
      logger.info("Tja, war wohl nix");
      return new ResponseEntity(new Message("password does not match the confirmation"), HttpStatus.CONFLICT);
    }

    Message message = authService.createUser(signUpForm);
    return new ResponseEntity(message, HttpStatus.CREATED);
  }

  @GetMapping("/mist")
  public String mist() {
    return "Hi du verdammter Mistkerl";
  }

  @PostMapping("/reset_password")
  public ResponseEntity resetPassword(@RequestBody PasswordResetForm passwordResetForm) {
    Message message = userService.createPasswordResetTokenForUser(passwordResetForm.getEmail());
    if (message.getTrueOrFalse() == false) {
      return new ResponseEntity(message, HttpStatus.NOT_FOUND);
    } else {
      return new ResponseEntity(message, HttpStatus.OK);
    }
  }

  @GetMapping(value = "/reset_password/{token}/edit")
  @ResponseStatus
  public ResponseEntity resetPassword(@PathVariable("token") String base64Token, @RequestParam("email") String email) {
    boolean tokenNotExpired = userService.checkResetToken(base64Token, email);
    HttpHeaders headers = new HttpHeaders();
    headers.add("checked", "AuthRestApi");
    if (tokenNotExpired == false) {
      return ResponseEntity.unprocessableEntity().headers(headers).body("tja, abjeloofen");
    } else if (tokenNotExpired) {
      return ResponseEntity.accepted().headers(headers).body("perfekt");
    } else {
      return ResponseEntity.badRequest().headers(headers).body("mann");
    }
  }

  @PutMapping("/reset_password/{token}")
  @ResponseStatus
  public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetForm passwordResetForm, @PathVariable("token") String base64Token, @RequestParam("email") String email) {
    Message message = userService.resetPassword(passwordResetForm, base64Token, email);
    HttpStatus status;
    if (message.getTrueOrFalse()) {
      status = HttpStatus.OK;
    } else {
      status = HttpStatus.BAD_REQUEST;
    }
    return new ResponseEntity(message, status);
  }

}
