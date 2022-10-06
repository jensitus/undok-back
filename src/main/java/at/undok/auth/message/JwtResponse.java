package at.undok.auth.message;

import at.undok.auth.model.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {

  private String token;
  private String type = "Bearer";
  private UserDto userDto;

  public JwtResponse(String accessToken) {
    this.token = accessToken;
  }

  public JwtResponse(UserDto userDto) {
    this.userDto = userDto;
  }

}
