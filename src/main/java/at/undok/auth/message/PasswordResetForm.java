package at.undok.auth.message;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetForm {

  @NotBlank
  private String email;

  private String password;

  private String password_confirmation;

}
