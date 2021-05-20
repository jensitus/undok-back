package org.service.b.auth.dto;

import lombok.Data;
import lombok.ToString;
import org.service.b.auth.validation.NewPasswordMatch;
import org.service.b.auth.validation.annotation.PasswordMatches;
import org.service.b.auth.validation.annotation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@ToString
@PasswordMatches
public class SignUpDto implements NewPasswordMatch {

  @NotBlank
  @Size(min = 3, max = 50)
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

  @Override
  public String getPassword() {
    return password;
  }


  @Override
  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }


}
