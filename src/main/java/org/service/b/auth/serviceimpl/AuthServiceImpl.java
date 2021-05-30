package org.service.b.auth.serviceimpl;

import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.service.b.auth.dto.ConfirmAccountDto;
import org.service.b.auth.dto.LoginDto;
import org.service.b.auth.dto.SignUpDto;
import org.service.b.auth.dto.UserDto;
import org.service.b.auth.message.Message;
import org.service.b.auth.model.Role;
import org.service.b.auth.model.RoleName;
import org.service.b.auth.model.User;
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
    public UserDto createUser(SignUpDto signUpDto) {
        Role uRole;
        if (signUpDto.isAdmin()) {
            uRole = roleRepo.findByName(RoleName.ADMIN).orElseThrow(() -> new RuntimeException("The Fucking Role couldn't be found, sorry"));
        } else {
            uRole = roleRepo.findByName(RoleName.USER).orElseThrow(() -> new RuntimeException("No Role Today my Love is gone away"));
        }
        String confirmationToken = UUID.randomUUID().toString();
        User user = new User(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), encoder.encode(signUpDto.getPassword()), confirmationToken, LocalDateTime.now());
        log.info("The Confirmation Token: ");
        log.info(confirmationToken);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(uRole);
        user.setRoles(roleSet);
        user.setAdmin(signUpDto.isAdmin());
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
    public Message confirmAccount(ConfirmAccountDto confirmAccountDto) {
        if (!confirmAccountDto.getPassword().equals(confirmAccountDto.getPasswordConfirmation())) {
            return new Message("Password and confirmation do not match");
        }
        log.info(confirmAccountDto.toString());
        if (!userService.checkIfTokenExpired(confirmAccountDto.getConfirmationToken(), confirmAccountDto.getEmail(), "confirm")) {
            throw new RuntimeException("Sorry, token expired");
        }
        User user = userRepo.findByEmail(confirmAccountDto.getEmail());
        user.setPassword(encoder.encode(confirmAccountDto.getPassword()));
        user.setConfirmed(Boolean.TRUE);
        userRepo.save(user);
        return new Message("User successfully confirmed");
    }
}
