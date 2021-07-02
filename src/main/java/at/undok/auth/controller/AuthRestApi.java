package at.undok.auth.controller;

import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.service.AuthService;
import at.undok.auth.service.UserService;
import at.undok.auth.security.JwtProvider;
import at.undok.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RestController
@RequestMapping("/service/auth")
@Validated
public class AuthRestApi {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        UserDto userDto = authService.getUserDtoWithJwt(loginDto);
        if (Boolean.TRUE.equals(userDto.getConfirmed())) {
            return new ResponseEntity<>(new JwtResponse(userDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("Account is not confirmed"), HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        }
    }

    @PostMapping(value = "/signup", consumes = {})
    public ResponseEntity<Message> registerUser(@Valid @RequestBody SignUpDto signUpDto) {
        if (userRepo.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>(new Message("It's a pity -> but this Email is already in use!"), HttpStatus.BAD_REQUEST);
        }
        if (!signUpDto.getPasswordConfirmation().equals(signUpDto.getPassword())) {
            return new ResponseEntity<>(new Message("password does not match the confirmation"), HttpStatus.CONFLICT);
        }
        authService.createUserAfterSignUp(signUpDto);
        return new ResponseEntity<>(new Message("user created"), HttpStatus.CREATED);
    }

    @GetMapping("/mist")
    public String mist() {
        return "Hi du verdammter Mistkerl";
    }

    @PostMapping("/reset_password")
    public ResponseEntity<Message> resetPassword(@RequestBody PasswordResetForm passwordResetForm) {
        Message message = userService.createPasswordResetTokenForUser(passwordResetForm.getEmail());
        if (!message.getRedirect()) {
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/reset_password/{token}/edit")
    @ResponseStatus
    public ResponseEntity<String> resetPassword(@PathVariable("token") String base64Token, @RequestParam("email") String email) {
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

    @PutMapping("/reset_password/{token}")
    @ResponseStatus
    public ResponseEntity<Message> resetPassword(@Valid @RequestBody PasswordResetForm passwordResetForm, @PathVariable("token") String base64Token, @RequestParam("encodedEmail") String email) {
        Message message = userService.resetPassword(passwordResetForm, base64Token, email);
        HttpStatus status;
        if (message.getRedirect()) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/{token}/{confirm}/{encoded_email}")
    public ResponseEntity<Message> checkTheConfirmationData(@PathVariable("token") String encodedToken, @PathVariable("confirm") String confirm, @PathVariable("encoded_email") String encodedEmail) {
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

    @PostMapping("/{token}/set_new_password")
    public ResponseEntity<Message> setNewPW(@RequestBody ConfirmAccountForm confirmAccountForm) {
        return new ResponseEntity<>(authService.confirmAccount(confirmAccountForm), HttpStatus.OK);
    }

}
