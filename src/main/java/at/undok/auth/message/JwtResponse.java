package at.undok.auth.message;

import at.undok.auth.model.dto.UserDto;

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

  public String getAccessToken() {
    return token;
  }

  public void setAccessToken(String accessToken) {
    this.token = accessToken;
  }

  public String getTokeType() {
    return type;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

  public UserDto getUserDto() {
    return userDto;
  }

  public void setUserDto(UserDto userDto) {
    this.userDto = userDto;
  }
}
