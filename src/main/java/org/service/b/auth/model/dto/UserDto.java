package org.service.b.auth.model.dto;

import lombok.Data;
import lombok.ToString;
import org.service.b.auth.model.entity.Role;

import java.time.LocalDateTime;
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

  private LocalDateTime createdAt;

  private boolean admin;

  private boolean locked;


}
