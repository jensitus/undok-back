package at.undok.auth.controller;

import at.undok.auth.api.SignUpApi;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.service.AuthService;
import at.undok.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class SignUpController implements SignUpApi {

    @Value("${auth-api.signup.enabled:false}")
    private boolean signUpEnabled;

    private final UserRepo userRepo;

    private final AuthService authService;

    public SignUpController(UserRepo userRepo, AuthService authService) {
        this.userRepo = userRepo;
        this.authService = authService;
    }

    @Override
    public ResponseEntity registerUser(SignUpDto signUpDto) {
        Message message;
        if (signUpEnabled) {
            return signUp(signUpDto);
        } else {
            message = new Message("Selbstregistrierung nicht aktiv, bitte bei Undok anfragen", null);
            return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
        }
    }

    private ResponseEntity signUp(SignUpDto signUpDto) {

        if (userRepo.existsByUsername(signUpDto.getUsername())) {
            return new ResponseEntity<>(new Message("Damn -> this Username is already taken"), HttpStatus.CONFLICT);
        }
        if (userRepo.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>(new Message("It's a pity -> but this Email is already in use!"), HttpStatus.CONFLICT);
        }
        if (!signUpDto.getPasswordConfirmation().equals(signUpDto.getPassword())) {
            return new ResponseEntity<>(new Message("password does not match the confirmation"), HttpStatus.CONFLICT);
        }
        var userDto = authService.createUserAfterSignUp(signUpDto);

        return ResponseEntity.created(URI.create("/service/users/by_username/" + userDto.getUsername())).body(new Message("user created"));
    }

}
