package org.service.b.auth.dto;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class UserDto {

  private UUID id;

  private String username;

  private String email;

  private String accessToken;

  private Boolean confirmed;


}
