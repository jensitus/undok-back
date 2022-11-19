package at.undok.auth.controller;

import at.undok.auth.api.AuthApi;
import at.undok.auth.message.JwtResponse;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.security.JwtProvider;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.common.exception.UserNotFoundException;
import at.undok.common.message.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuthController implements AuthApi {

    final
    AuthenticationManager authenticationManager;

    final
    UserRepo userRepo;

    final
    JwtProvider jwtProvider;

    private final UserService userService;

    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, UserRepo userRepo, JwtProvider jwtProvider, UserService userService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
        this.authService = authService;
    }

    @Override
    public ResponseEntity<JwtResponse> authenticateUser(LoginDto loginDto) {
        UserDto userDto = authService.getUserDtoWithSecondFactorJwt(loginDto);
        if (Boolean.TRUE.equals(userDto.getConfirmed())) {
            JwtResponse jwtResponse = new JwtResponse(userDto);
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } else {
            throw new RuntimeException("sorry");
            //return new ResponseEntity<>(new Message("Account is not confirmed"), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        }
    }

    @Override
    public String mist() {
        return "Hi du verdammter Mistkerl";
    }

    @Override
    public ResponseEntity<Message> resetPassword(PasswordResetForm passwordResetForm) {
        Message message = userService.createPasswordResetTokenForUser(passwordResetForm.getEmail());
        if (!message.getRedirect()) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @ResponseStatus
    @Override
    public ResponseEntity<String> resetPassword(String base64Token, String email) {
        boolean tokenNotExpired = userService.checkIfTokenExpired(base64Token, email, null);
        HttpHeaders headers = new HttpHeaders();
        headers.add("checked", "AuthRestApi");
        if (!tokenNotExpired) {
            return ResponseEntity.unprocessableEntity().headers(headers).body("tja, abjeloofen");
        } else if (tokenNotExpired) {
            return ResponseEntity.accepted().headers(headers).body("perfekt");
        } else {
            return ResponseEntity.badRequest().headers(headers).body("mann");
        }
    }

    @ResponseStatus
    @Override
    public ResponseEntity<Message> resetPassword(PasswordResetForm passwordResetForm, String base64Token, String email) {
        Message message = userService.resetPassword(passwordResetForm, base64Token, email);
        HttpStatus status;
        if (message.getRedirect()) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(message, status);
    }

    @Override
    public ResponseEntity<Message> checkTheConfirmationData(String encodedToken, String confirm, String encodedEmail) {
        boolean tokenNotExpired = userService.checkIfTokenExpired(encodedToken, encodedEmail, confirm);
        boolean changePassword = userService.checkIfPasswordHasToBeChanged(encodedToken, encodedEmail);
        if (tokenNotExpired && changePassword) {
            // message = authService.confirmAccount(base64Token, email);
            return new ResponseEntity<>(new Message("token valid"), HttpStatus.OK);
        } else if (tokenNotExpired) {
            authService.confirmAccountWithoutPWChange(encodedToken, encodedEmail);
            return new ResponseEntity<>(new Message("token valid and password has not to be changed", true), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("expired", false), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public ResponseEntity<Message> setNewPW(ConfirmAccountForm confirmAccountForm) {
        return new ResponseEntity<>(authService.confirmAccount(confirmAccountForm), HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Message> handleException(UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(String.format("user not found: %s", userNotFoundException.getUsername())));
    }

}
