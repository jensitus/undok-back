package at.undok.auth.model.dto;

import at.undok.auth.validation.NewPasswordMatch;
import at.undok.auth.validation.annotation.PasswordMatches;
import lombok.Data;
import lombok.ToString;

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
