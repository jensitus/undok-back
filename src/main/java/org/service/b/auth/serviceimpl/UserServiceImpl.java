package org.service.b.auth.serviceimpl;

import io.jsonwebtoken.impl.Base64Codec;
import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.model.PasswordResetToken;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.PasswordResetTokenRepo;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.UserService;
import org.service.b.auth.security.JwtProvider;
import org.service.b.common.mailer.service.ServiceBOrgMailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

  @Override
  public void createPasswordResetTokenForUser(User user) {
    String token = UUID.randomUUID().toString();
    String base64token = Base64Codec.BASE64.encode(token);
    LocalDateTime localDateTime = LocalDateTime.now();
    PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, localDateTime);
    passwordResetTokenRepo.save(passwordResetToken);
    String url = "http://localhost:4200/reset-password/" + base64token + "/edit?email=" + user.getEmail();
    String subject = "[service-b.org] reset instructions";
    String text = "click the fucking url below";
    serviceBOrgMailer.getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
  }

  @Override
  public boolean checkResetToken(String base64Token, String email) {
    return checkIfResetTokenExpired(base64Token, email);
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
