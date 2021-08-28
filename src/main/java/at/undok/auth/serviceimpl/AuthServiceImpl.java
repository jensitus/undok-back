package at.undok.auth.serviceimpl;

import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.common.mailer.impl.UndokMailer;
import at.undok.common.util.EmailStuff;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.common.message.Message;
import at.undok.auth.model.entity.Role;
import at.undok.auth.model.entity.User;
import at.undok.auth.repository.RoleRepo;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.security.JwtProvider;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.common.encryption.AttributeEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private UserService userService;

    @Autowired
    private UndokMailer undokMailer;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AttributeEncryptor attributeEncryptor;

    @Override
    public UserDto getUserDtoWithJwt(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        User user = userRepo.findByUsername(loginDto.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with -> username or email: " + loginDto.getUsername()));
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setAccessToken(jwt);
        return userDto;
    }

    @Override
    public UserDto createUserAfterSignUp(SignUpDto signUpDto) {
        String confirmationToken = UUID.randomUUID().toString();
        User user = new User(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), encoder.encode(signUpDto.getPassword()), confirmationToken, LocalDateTime.now(), LocalDateTime.now());
        user.setChangePassword(false);
        userRepo.save(user);
        createConfirmationMail(user, confirmationToken);
        return modelMapper.map(user, UserDto.class);
    }

    private Message createConfirmationMail(User user, String confirmationToken) {
        String url = createConfirmationUrl(user.getEmail(), confirmationToken);
        String subject = EmailStuff.SUBJECT_PREFIX + "confirm account";
        String text = "click the link below within the next 2 hours, after this it will expire";
        log.info(url);
        undokMailer.getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
        return new Message("We've sent you a message confirming your", true);
    }

    @Override
    public Message confirmAccount(ConfirmAccountForm confirmAccountForm) {
        if (!userService.checkIfTokenExpired(confirmAccountForm.getConfirmationToken(), confirmAccountForm.getEmail(), "confirm")) {
            throw new RuntimeException("Sorry, token expired");
        }
        if (!confirmAccountForm.getPassword().equals(confirmAccountForm.getPasswordConfirmation())) {
            return new Message("Password and confirmation do not match");
        }
        log.info(confirmAccountForm.toString());
        User user = userRepo.findByEmail(attributeEncryptor.decodeUrlEncoded(confirmAccountForm.getEmail()));
        user.setPassword(encoder.encode(confirmAccountForm.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleService.getUserRole());
        user.setConfirmed(true);
        userRepo.save(user);
        return new Message("User successfully confirmed");
    }

    @Override
    public void confirmAccountWithoutPWChange(String encodedToken, String encodedEmail) {
        String confirmationToken = attributeEncryptor.decodeUrlEncoded(encodedToken);
        String email = attributeEncryptor.decodeUrlEncoded(encodedEmail);
        User user = userRepo.findByEmailAndConfirmationToken(email, confirmationToken);
        user.setConfirmed(true);
        userRepo.save(user);
    }

    @Override
    public String createUserViaAdmin(CreateUserForm createUserForm) {
        log.info(createUserForm.toString());
        String confirmationToken = UUID.randomUUID().toString();
        User user = new User(createUserForm.getUsername(), createUserForm.getEmail(), confirmationToken, LocalDateTime.now(), LocalDateTime.now());
        user.setAdmin(createUserForm.isAdmin());
        user.setConfirmationTokenCreatedAt(LocalDateTime.now());
        user.setChangePassword(true);
        createConfirmationMail(user, confirmationToken);
        userRepo.save(user);
        return createConfirmationUrl(user.getEmail(), confirmationToken);
    }

    @Override
    public String createConfirmationUrl(String email, String confirmationToken) {
        String encryptedEmail = attributeEncryptor.encodeWithUrlEncoder(email);
        String encryptedToken = attributeEncryptor.encodeWithUrlEncoder(confirmationToken);
        String confUrl = EmailStuff.DOMAIN_URL_FOR_DEV + "/auth/" + encryptedToken + "/confirm/" + encryptedEmail;
        log.info(confUrl);
        return confUrl;
    }

    private void notifyAdminAboutNewUser() {

    }

}
