package at.undok.auth.api;

import at.undok.auth.model.dto.ChangePwDto;
import at.undok.auth.model.dto.SetAdminDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.common.message.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RequestMapping("/service/users")
public interface UserApi {

    @GetMapping("/all")
    public List<UserDto> getAllUsers();

    @GetMapping("/principle/{username}")
    public UserDetails getSpecialUser(@PathVariable String username);

    @GetMapping("/by_username/{username}")
    public UserDto getUserByUsername(@PathVariable("username") String username);

    @PostMapping("/auth/check_auth_token")
    public ResponseEntity<Message> checkTheAuthToken(@RequestBody String token);

    @PostMapping("/auth/password_resets")
    public ResponseEntity<Message> password_resets(@RequestBody String email);

    @PostMapping("/changepw")
    public ResponseEntity changePw(@Valid @RequestBody ChangePwDto changePwDto);

    @PostMapping("/set-admin/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Message setAdminFlag(@PathVariable("user_id") UUID userId, @RequestBody SetAdminDto setAdminDto);

    @PostMapping("/create-user-via-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity createUserViaAdmin(@RequestBody CreateUserForm createUserForm);

}
