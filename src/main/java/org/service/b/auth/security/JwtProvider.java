package org.service.b.auth.security;

import io.jsonwebtoken.*;
import org.service.b.auth.model.User;
import org.service.b.auth.serviceimpl.UserPrinciple;
import org.service.b.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

  @Value("${service.b.org.app.jwtSecret}")
  private String jwtSecret;

  @Value("${service.b.org.app.jwtExpiration}")
  private int jwtExpiration;

  @Value("${service.b.org.app.jwtResetExpiration}")
  private int resetExpiration;

  public String generateJwtToken(Authentication authentication) {
    UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
    return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpiration)).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
  }

  public String generatePasswordResetToken(User user) {
    return Jwts.builder().setSubject(user.getUsername()).setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + resetExpiration)).signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public Message validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return new Message(true);
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature -> Message: {} ", e);
      return new Message(e.toString(), false);
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token -> Message: {}", e);
      return new Message(e.toString(), false);
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token -> Message: {}", e);
      return new Message(e.toString(), false);
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token -> Message: {}", e);
      return new Message(e.toString(), false);
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty -> Message: {}", e);
      return new Message(e.toString(), false);
    }
    // return new Message(false);
  }

}
