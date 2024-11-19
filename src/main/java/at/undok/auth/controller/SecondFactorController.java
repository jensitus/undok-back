package at.undok.auth.controller;

import at.undok.auth.api.SecondFactorApi;
import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondFactorController implements SecondFactorApi {

    private final AuthService authService;

    public SecondFactorController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<JwtResponse> secondFactor(SecondFactorForm secondFactorForm) {
        UserDto userDto = authService.getUserDtoWithRealJwt(secondFactorForm);
        return new ResponseEntity<>(new JwtResponse(userDto), HttpStatus.OK);
    }

}
