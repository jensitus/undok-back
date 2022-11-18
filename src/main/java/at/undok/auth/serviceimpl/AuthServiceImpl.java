package at.undok.auth.serviceimpl;

import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.entity.Role;
import at.undok.auth.model.entity.RoleName;
import at.undok.auth.model.entity.TwoFactor;
import at.undok.auth.model.entity.User;
import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.auth.repository.RoleRepo;
import at.undok.auth.repository.TwoFactorRepo;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.security.JwtProvider;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.exception.UserNotFoundException;
import at.undok.common.mailer.impl.UndokMailer;
import at.undok.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Value("${service.b.org.app.jwtExpiration}")
    private int jwtExpiration;

    @Value("${at.undok.secondFactorJwtExpiration}")
    private int secondFactorJwtExpiration;

    final
    AuthenticationManager authenticationManager;

    final
    JwtProvider jwtProvider;

    private final UserRepo userRepo;

    private final ModelMapper modelMapper;

    final
    PasswordEncoder encoder;

    final
    RoleRepo roleRepo;

    private final UserService userService;

    private final UndokMailer undokMailer;

    private final RoleService roleService;

    private final AttributeEncryptor attributeEncryptor;

    private final TwoFactorRepo twoFactorRepo;

    public AuthServiceImpl(JwtProvider jwtProvider, AuthenticationManager authenticationManager, UserRepo userRepo, ModelMapper modelMapper, PasswordEncoder encoder, RoleRepo roleRepo, UserService userService, UndokMailer undokMailer, RoleService roleService, AttributeEncryptor attributeEncryptor, TwoFactorRepo twoFactorRepo) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
        this.userService = userService;
        this.undokMailer = undokMailer;
        this.roleService = roleService;
        this.attributeEncryptor = attributeEncryptor;
        this.twoFactorRepo = twoFactorRepo;
    }

    @Override
    public UserDto getUserDtoWithSecondFactorJwt(LoginDto loginDto) {
        removeRole(loginDto.getUsername(), RoleName.ROLE_USER);
        addRole(loginDto.getUsername(), RoleName.ROLE_SECOND_FACTOR);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwt(
                new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials()),
                secondFactorJwtExpiration
        );
        UserDto userDto = userDtoWithJwt(jwt, loginDto.getUsername());
        generateAndPersist2FactorToken(userDto);
        log.info("jwt: " + jwt);
        return userDto;
    }

    private UserDto userDtoWithJwt(String jwt, String username) {
        User user = userRepo.findByUsername(
                        username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with -> username or email: " + username)
                );
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setAccessToken(jwt);
        return userDto;
    }

    private void generateAndPersist2FactorToken(UserDto userDto) {
        String secondFactorToken = generateSecondFactorToken();
        deleteOldTwoFactorTokens(userDto.getId());
        TwoFactor twoFactor = new TwoFactor();
        twoFactor.setToken(secondFactorToken);
        twoFactor.setExpiration(LocalDateTime.now().plusMinutes(5));
        twoFactor.setUserId(userDto.getId());
        twoFactor.setCreatedAt(LocalDateTime.now());
        twoFactorRepo.save(twoFactor);
        send2FactorTokenToUser(secondFactorToken, userDto.getEmail());
    }

    private void send2FactorTokenToUser(String secondFactorToken, String email) {
        undokMailer.send2FactorTokenToUser(secondFactorToken, email);
    }

    @Override
    public UserDto getUserDtoWithRealJwt(SecondFactorForm secondFactorForm) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String jwt = null;
        if (checkSecondFactorToken(secondFactorForm, userPrinciple.getId())) {
            jwt = jwtProvider.generateJwt(authentication, jwtExpiration);
        }
        addRole(userPrinciple.getUsername(), RoleName.ROLE_USER);
        removeRole(userPrinciple.getUsername(), RoleName.ROLE_SECOND_FACTOR);
        return userDtoWithJwt(jwt, userPrinciple.getUsername());
    }

    @Override
    public UserDto createUserAfterSignUp(SignUpDto signUpDto) {
        String confirmationToken = UUID.randomUUID().toString();
        User user = new User(signUpDto.getUsername(), signUpDto.getEmail().toLowerCase(), encoder.encode(signUpDto.getPassword()), confirmationToken, LocalDateTime.now(), LocalDateTime.now());
        user.setChangePassword(false);
        user.setConfirmed(false);
        userRepo.save(user);
        undokMailer.createConfirmationMail(user, confirmationToken);
        return modelMapper.map(user, UserDto.class);
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
        Message m = undokMailer.createConfirmationMail(user, confirmationToken);
        userRepo.save(user);
        return m.getText();
    }

    private String generateSecondFactorToken() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                     .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                     .limit(targetStringLength)
                     .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                     .toString();
    }

    private boolean checkSecondFactorToken(SecondFactorForm secondFactorForm, UUID userId) {
        TwoFactor twoFactor = twoFactorRepo.findByUserIdAndToken(userId, secondFactorForm.getToken());
        return twoFactor != null && LocalDateTime.now().isBefore(twoFactor.getExpiration());
    }

    private void addRole(String username, RoleName roleName) {
        User user = userRepo.findByUsername(username).get();
        Set<Role> roles = user.getRoles();
        roles.add(roleRepo.findByName(roleName).get());
        user.setRoles(roles);
        userRepo.save(user);
    }

    private void removeRole(String username, RoleName roleName) {
        User user;
        try {
            user = userRepo.findByUsername(username).orElseThrow();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("we couldn't find a user " + username + " in the database");
        }
        Set<Role> roles = user.getRoles();
        roles.remove(roleRepo.findByName(roleName).orElse(null));
        user.setRoles(roles);
        userRepo.save(user);
    }

    private void deleteOldTwoFactorTokens(UUID userId) {
        List<TwoFactor> twoFactorList = twoFactorRepo.findByUserId(userId);
        twoFactorRepo.deleteAll(twoFactorList);
    }

}
