package org.service.b.auth.serviceimpl;

import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.service.b.auth.model.form.ConfirmAccountForm;
import org.service.b.auth.model.dto.LoginDto;
import org.service.b.auth.model.dto.SignUpDto;
import org.service.b.auth.model.dto.UserDto;
import org.service.b.auth.message.Message;
import org.service.b.auth.model.entity.Role;
import org.service.b.auth.model.entity.RoleName;
import org.service.b.auth.model.entity.User;
import org.service.b.auth.model.form.CreateUserForm;
import org.service.b.auth.repository.RoleRepo;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.security.JwtProvider;
import org.service.b.auth.service.AuthService;
import org.service.b.auth.service.UserService;
import org.service.b.common.mailer.service.ServiceBOrgMailer;
import org.service.b.common.util.EmailStuff;
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
    private ServiceBOrgMailer serviceBOrgMailer;

    @Autowired
    private RoleService roleService;

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
        User user = new User(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), encoder.encode(signUpDto.getPassword()), confirmationToken, LocalDateTime.now());
        log.info(confirmationToken);
        userRepo.save(user);
        createConfirmationMail(user, confirmationToken);
        return modelMapper.map(user, UserDto.class);
    }

    private Message createConfirmationMail(User user, String confirmationToken) {
        String base64token = Base64Codec.BASE64.encode(confirmationToken);
        String url = EmailStuff.DOMAIN_URL_FOR_DEV + "/auth/" + base64token + "/confirm?email=" + user.getEmail();
        String subject = EmailStuff.SUBJECT_PREFIX + "confirm account";
        String text = "click the link below within the next 2 hours, after this it will expire";
        log.info(url);
        // serviceBOrgMailer.getTheMailDetails(user.getEmail(), subject, text, user.getUsername(), url);
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
        User user = userRepo.findByEmail(confirmAccountForm.getEmail());
        user.setPassword(encoder.encode(confirmAccountForm.getPassword()));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleService.getConfirmedRole());
        userRepo.save(user);
        return new Message("User successfully confirmed");
    }

    @Override
    public void createUserViaAdmin(CreateUserForm createUserForm) {
        String confirmationToken = UUID.randomUUID().toString();
        User user = new User(createUserForm.getUsername(), createUserForm.getEmail(), confirmationToken, LocalDateTime.now());
        user.setAdmin(createUserForm.isAdmin());
        user.setConfirmationTokenCreatedAt(LocalDateTime.now());
        createConfirmationMail(user, confirmationToken);
        userRepo.save(user);
    }

    private void notifyAdminAboutNewUser() {

    }

}
