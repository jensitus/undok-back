package at.undok.auth.api;

import at.undok.auth.model.dto.SignUpDto;
import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/service/auth")
public interface SignUpApi {

    @PostMapping(value = "/signup")
    public ResponseEntity<Message> registerUser(@Valid @RequestBody SignUpDto signUpDto);

}
