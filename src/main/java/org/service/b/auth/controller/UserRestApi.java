package org.service.b.auth.controller;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.security.JwtProvider;
import org.service.b.auth.service.UserService;
import org.service.b.auth.serviceimpl.UserDetailsServiceImpl;
import org.service.b.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserRestApi {

  private static final Logger logger = LoggerFactory.getLogger(UserRestApi.class);

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  JwtProvider jwtProvider;

  @GetMapping("/all")
  public List<UserDto> getAllUsers() {
    List<UserDto> userDtoList = userService.getAll();
    return userDtoList;
  }

  @GetMapping("/principle/{username}")
  public UserDetails getSpecialUser(@PathVariable String username) {
    return userDetailsService.loadUserByUsername(username);
  }

  @PostMapping("/auth/check_auth_token")
  public ResponseEntity checkTheAuthToken(@RequestBody String token) {
    logger.info("hier sind wir jetzt beim auth token check " + token);
    Message message = jwtProvider.validateJwtToken(token);
    logger.info("message {}", message.toString());
    if (jwtProvider.validateJwtToken(token).getTrueOrFalse() == true) {
      logger.info("passt" + message);
      return new ResponseEntity(message, HttpStatus.OK);
    } else {
      logger.info("naja" + message);
      return new ResponseEntity(message, HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/auth/password_resets")
  public ResponseEntity password_resets(@RequestBody String email) {
    logger.info(email);
    userService.createPasswordResetTokenForUser(email);
    return new ResponseEntity(new Message("jess god damn"), HttpStatus.OK);
  }

}
