package org.service.b.auth.dto;

import lombok.Data;
import lombok.ToString;
import org.service.b.auth.model.Role;

import java.util.Set;
import java.util.UUID;

@Data
@ToString
public class UserDto {

  private UUID id;

  private String username;

  private String email;

  private String accessToken;

  private Boolean confirmed;

  private Set<Role> roles;

  private boolean admin;

  private boolean locked;


}
