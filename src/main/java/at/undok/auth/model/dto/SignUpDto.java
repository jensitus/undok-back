package at.undok.auth.model.dto;

import at.undok.auth.validation.annotation.ValidEmail;
import lombok.Data;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@ToString
public class SignUpDto {

  private String username;

  @NotBlank
  @Size(max = 60)
  @NotNull
  @ValidEmail
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 70)
  private String password;

  private String passwordConfirmation;

  private boolean admin;

}
