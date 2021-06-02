package org.service.b.auth.controller;

import org.service.b.auth.model.form.ConfirmAccountForm;
import org.service.b.auth.model.dto.UserDto;
import org.service.b.auth.message.JwtResponse;
import org.service.b.auth.model.dto.LoginDto;
import org.service.b.auth.message.PasswordResetForm;
import org.service.b.auth.model.dto.SignUpDto;
import org.service.b.auth.model.form.CreateUserForm;
import org.service.b.auth.repository.UserRepo;
import org.service.b.auth.service.AuthService;
import org.service.b.auth.service.UserService;
import org.service.b.auth.security.JwtProvider;
import org.service.b.auth.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
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
//    if (!signUpDto.getPasswordConfirmation().equals(signUpDto.getPassword())) {
//      return new ResponseEntity<>(new Message("password does not match the confirmation"), HttpStatus.CONFLICT);
//    }
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
    public ResponseEntity<Message> resetPassword(@Valid @RequestBody PasswordResetForm passwordResetForm, @PathVariable("token") String base64Token, @RequestParam("email") String email) {
        Message message = userService.resetPassword(passwordResetForm, base64Token, email);
        HttpStatus status;
        if (message.getRedirect()) {
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(message, status);
    }

    @GetMapping("/{token}/{confirm}")
    public ResponseEntity<Message> confirmAccount(@PathVariable("token") String base64Token, @PathVariable("confirm") String confirm, @Email @RequestParam("email") String email) {
        boolean tokenNotExpired = userService.checkIfTokenExpired(base64Token, email, confirm);
        if (tokenNotExpired) {
            // message = authService.confirmAccount(base64Token, email);
            return new ResponseEntity<>(new Message("token valid"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("expired", false), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("/{token}/set_new_password")
    public ResponseEntity<Message> setNewPW(@RequestBody ConfirmAccountForm confirmAccountForm) {
        return new ResponseEntity<>(authService.confirmAccount(confirmAccountForm), HttpStatus.OK);
    }

    @PostMapping("/create-user-via-admin")
    public ResponseEntity createUserViaAdmin(@RequestBody CreateUserForm createUserForm) {
        authService.createUserViaAdmin(createUserForm);
        return new ResponseEntity(new Message("cool"), HttpStatus.OK);
    }

}
