package org.service.b.auth.controller;

import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.security.JwtProvider;
import org.service.b.auth.service.UserService;
import org.service.b.auth.serviceimpl.UserDetailsServiceImpl;
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

  @PostMapping("/check_auth_token")
  public boolean checkTheAuthToken(@RequestBody String token) {
    logger.info("hier sind wir jetzt beim auth token check " + token);
    if (jwtProvider.validateJwtToken(token)) {
      logger.info("passt");
      return true;
    } else {
      logger.info("naja");
      return false;
    }
  }

}
