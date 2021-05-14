package org.service.b.auth.dto;

import lombok.Data;
import lombok.ToString;
import org.service.b.auth.validation.NewPasswordMatch;
import org.service.b.auth.validation.annotation.PasswordMatches;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@ToString
@PasswordMatches
public class ChangePwDto implements NewPasswordMatch {

  private UUID userId;

  @NotBlank
  @Size(min = 6, max = 70)
  private String password;

  private String passwordConfirmation;

  private String oldPassword;

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getPasswordConfirmation() {
    return passwordConfirmation;
  }

}
