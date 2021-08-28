package at.undok.auth.serviceimpl;

import at.undok.common.mailer.impl.UndokMailer;
import at.undok.common.util.EmailStuff;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import at.undok.auth.model.dto.ChangePwDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.entity.PasswordResetToken;
import at.undok.auth.model.entity.Role;
import at.undok.auth.model.entity.User;
import at.undok.auth.repository.PasswordResetTokenRepo;
import at.undok.auth.repository.RoleRepo;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.service.UserService;
import at.undok.auth.security.JwtProvider;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired
  private PasswordResetTokenRepo passwordResetTokenRepo;

  @Autowired
  private UndokMailer undokMailer;

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  private RoleRepo roleRepo;

  @Autowired
  private RoleService roleService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private AttributeEncryptor attributeEncryptor;

  @Override
  public Message createPasswordResetTokenForUser(String email) {
    User user = userRepo.findByEmail(email.toLowerCase());
    if (user == null) {
      log.info("no user with email: " + email + " found");
      return new Message("Die Emailadresse gibt es nicht", false);
    }
    String token = UUID.randomUUID().toString();
    String base64token = Base64Codec.BASE64.encode(token);
    LocalDateTime localDateTime = LocalDateTime.now();
    PasswordResetToken passwordResetToken = new PasswordResetToken(user, token, localDateTime);
    passwordResetTokenRepo.save(passwordResetToken);
    String encryptedEmail = attributeEncryptor.encodeWithUrlEncoder(user.getEmail());
    String url = EmailStuff.DOMAIN_FOR_URL + "/auth/reset_password/" + base64token + "/edit?email=" + encryptedEmail;
    log.info(url);
    String subject = EmailStuff.SUBJECT_PREFIX + " reset instructions";
    String text = "click the link below within the next 2 hours, after this it will expire";
    // serviceBOrgMailer.getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
    return new Message("We've sent you a message with reset instructions", true);
  }

  @Override
  public boolean checkIfTokenExpired(String base64Token, String encodedEmail, String confirm) {
    if ("confirm".equals(confirm)) {
      return checkIfConfirmTokenExpired(base64Token, encodedEmail);
    } else {
      return checkIfResetTokenExpired(base64Token, encodedEmail);
    }
  }

  public boolean checkIfPasswordHasToBeChanged(String encodedToken, String encodedEmail) {
    String confirmationToken = attributeEncryptor.decodeUrlEncoded(encodedToken);
    String email = attributeEncryptor.decodeUrlEncoded(encodedEmail);
    return userRepo.selectChangePasswordFromUser(email, confirmationToken);
  }

  @Override
  public Message resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email) {
    if (passwordResetForm.getPassword().equals(passwordResetForm.getPassword_confirmation())) {
      if (checkIfResetTokenExpired(base64Token, email)) {
        User user = userRepo.findByEmail(attributeEncryptor.decodeUrlEncoded(passwordResetForm.getEmail()));
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

  private boolean checkIfResetTokenExpired(String base64Token, String encodedEmail) {
    String token = Base64Codec.BASE64.decodeToString(base64Token);
    String decodedEmail = attributeEncryptor.decodeUrlEncoded(encodedEmail);
    User user = userRepo.findByEmail(decodedEmail);
    PasswordResetToken prt = passwordResetTokenRepo.findByTokenAndUserId(token, user.getId());
    LocalDateTime exp = prt.getExpiryDate();
    if (exp.plusHours(2).isBefore(LocalDateTime.now())) {
      log.info(exp.toString());
      return false;
    }
    return true;
  }

  private boolean checkIfConfirmTokenExpired(String base64Token, String encodedEmail) {
    String confirmationToken = attributeEncryptor.decodeUrlEncoded(base64Token);
    String email = attributeEncryptor.decodeUrlEncoded(encodedEmail);
    // User user = userRepo.findByEmailAndConfirmationToken(email, confirmationToken);
    LocalDateTime exp = userRepo.selectConfirmationTokenCreatedAt(email, confirmationToken);
    log.info("exp " + exp);
    return !exp.plusHours(2).isBefore(LocalDateTime.now());
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

  @Override
  public UserDto getById(UUID user_id) {
    User user = userRepo.getOne(user_id);
    return modelMapper.map(user, UserDto.class);
  }

  @Override
  public Message changePw(ChangePwDto changePwDto) {
    UserDto userDto = getById(changePwDto.getUserId());
    try {
      Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), changePwDto.getOldPassword()));
      User user = userRepo.getOne(changePwDto.getUserId());
      user.setPassword(encoder.encode(changePwDto.getPassword()));
      userRepo.save(user);
      return new Message("Bravo, Password successfully changed!", true);
    } catch (Exception e) {
      return new Message(e.getLocalizedMessage(), false);
    }
  }

  @Override
  public void setAdmin(UUID userId, boolean admin) {
    User user = userRepo.getOne(userId);
    Set<Role> userRoles = user.getRoles();
    Role adminRole = roleService.getAdminRole();
    if (admin == true) {
      userRoles.add(adminRole);
    } else {
      userRoles.remove(adminRole);
    }
    user.setAdmin(admin);
    user.setRoles(userRoles);
    userRepo.save(user);
  }

  @Override
  public UserDto getByUsername(String username) {
    Optional<User> byUsername = userRepo.findByUsername(username);
    return modelMapper.map(byUsername.get(), UserDto.class);
  }

}
