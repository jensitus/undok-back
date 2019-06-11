package org.service.b.auth.serviceimpl;

import io.jsonwebtoken.impl.Base64Codec;
import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.PasswordResetForm;
import org.service.b.auth.model.PasswordResetToken;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.PasswordResetTokenRepo;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.UserService;
import org.service.b.auth.security.JwtProvider;
import org.service.b.common.mailer.service.ServiceBOrgMailer;
import org.service.b.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private PasswordResetTokenRepo passwordResetTokenRepo;

  @Autowired
  private ServiceBOrgMailer serviceBOrgMailer;

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  PasswordEncoder encoder;

  @Override
  public Message createPasswordResetTokenForUser(String email) {
    User user = userRepo.findByEmail(email.toLowerCase());
    if (user == null) {
      logger.info("no user with email: " + email + " found");
      return new Message("Die Emailadresse gibt es nicht", false);
    }
    String token = UUID.randomUUID().toString();
    String base64token = Base64Codec.BASE64.encode(token);
    LocalDateTime localDateTime = LocalDateTime.now();
    PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, localDateTime);
    passwordResetTokenRepo.save(passwordResetToken);
    String url = "https://service-b.org/auth/reset_password/" + base64token + "/edit?email=" + user.getEmail();
    String subject = "[service-b.org] reset instructions";
    String text = "click the fucking link below";
    serviceBOrgMailer.getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
    return new Message("We've sent you a message with reset instructions", true);
  }

  @Override
  public boolean checkResetToken(String base64Token, String email) {
    return checkIfResetTokenExpired(base64Token, email);
  }

  @Override
  public Message resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email) {
    if (passwordResetForm.getPassword().equals(passwordResetForm.getPassword_confirmation())) {
      if (checkIfResetTokenExpired(base64Token, email)) {
        User user = userRepo.findByEmail(passwordResetForm.getEmail());
        user.setPassword(encoder.encode(passwordResetForm.getPassword()));
        userRepo.save(user);
        return new Message("toll", true);
      } else {
        return new Message("Die Zeit ist abgelaufen", false);
      }
    } else {
      return new Message("password and confirmation does not match", false);
    }
  }

  private boolean checkIfResetTokenExpired(String base64Token, String email) {
    String token = Base64Codec.BASE64.decodeToString(base64Token);
    logger.info("token check " + token);
    logger.info("email" + email);
    User user = userRepo.findByEmail(email);
    PasswordResetToken prt = passwordResetTokenRepo.findByTokenAndUserId(token, user.getId());
    logger.info("token and id " + prt.toString());
    LocalDateTime exp = prt.getExpiryDate();
    if (exp.plusHours(2).isBefore(LocalDateTime.now())) {
      logger.info(exp.toString());
      return false;
    }
    return true;
  }

  @Override
  public List<UserDto> getAll() {
    List<User> users = userRepo.findAll();
    List<UserDto> userDtoList = new ArrayList<>();
    for (User user : users) {
      userDtoList.add(modelMapper.map(user, UserDto.class));
    }
    return userDtoList;
  }

  @Override
  public UserDto getCurrentUser() {
    UserDto userDto;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserPrinciple userPrinciple = (UserPrinciple) auth.getPrincipal();
    userDto = modelMapper.map(userPrinciple, UserDto.class);
    return userDto;
  }



}
