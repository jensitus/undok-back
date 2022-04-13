package at.undok.auth.api;

import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/service/auth")
public interface AuthApi {

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto);

    @PostMapping("/second-factor")
    public ResponseEntity<?> secondFactor(@RequestBody SecondFactorForm secondFactorForm);

    @PostMapping(value = "/signup")
    public ResponseEntity<Message> registerUser(@Valid @RequestBody SignUpDto signUpDto);

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
