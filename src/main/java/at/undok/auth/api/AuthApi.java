package at.undok.auth.api;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequestMapping("/service/auth")
public interface AuthApi {

    @PostMapping("/login")
    ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto);


    @GetMapping("/mist")
    public String mist();

    @PostMapping("/reset_password")
    public ResponseEntity<Message> resetPassword(@RequestBody PasswordResetForm passwordResetForm);

    @GetMapping(value = "/reset_password/{token}/edit")
    public ResponseEntity<String> resetPassword(@PathVariable("token") String base64Token,
                                                @RequestParam("email") String email);

    @PutMapping("/reset_password/{token}")
    public ResponseEntity<Message> resetPassword(@Valid @RequestBody PasswordResetForm passwordResetForm,
                                                 @PathVariable("token") String base64Token,
                                                 @RequestParam("email") String email);

    @GetMapping("/{token}/{confirm}/{encoded_email}")
    public ResponseEntity<Message> checkTheConfirmationData(@PathVariable("token") String encodedToken,
                                                            @PathVariable("confirm") String confirm,
                                                            @PathVariable("encoded_email") String encodedEmail);

    @PostMapping("/{token}/set_new_password")
    public ResponseEntity<Message> setNewPW(@RequestBody ConfirmAccountForm confirmAccountForm);

}
