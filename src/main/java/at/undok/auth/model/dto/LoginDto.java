package at.undok.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

  @NotBlank
  @Size(min=3, max = 60)
  private String username;

  private String email;

  @NotBlank
  @Size(min = 6, max = 70)
  private String password;

}
