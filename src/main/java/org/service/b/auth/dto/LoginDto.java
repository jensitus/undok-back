package org.service.b.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginDto {

  @NotBlank
  @Size(min=3, max = 60)
  private String username;

  private String email;

  @NotBlank
  @Size(min = 6, max = 70)
  private String password;

}
