package at.undok.auth.security;

import at.undok.auth.exception.TokenExpiredException;
import at.undok.auth.serviceimpl.UserPrinciple;
import io.jsonwebtoken.*;
import at.undok.auth.model.entity.User;
import at.undok.common.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

  @Value("${service.b.org.app.jwtSecret}")
  private String jwtSecret;

  @Value("${service.b.org.app.jwtExpiration}")
  private int jwtExpiration;

  @Value("${at.undok.secondFactorJwtExpiration}")
  private int secondFactorJwtExpiration;

  @Value("${service.b.org.app.jwtResetExpiration}")
  private int resetExpiration;

  public String generateJwt(Authentication authentication, int expiration) {
    UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
    Map<String, Object> claims = new HashMap<>();
    String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    claims.put("roles", authorities);
    return Jwts.builder()
               .setSubject((userPrincipal.getUsername()))
               .setIssuedAt(new Date())
               .setExpiration(new Date((new Date()).getTime() + expiration))
               .claim("roles", authorities)
               .signWith(SignatureAlgorithm.HS512, jwtSecret)
               .compact();
  }

  public String generatePasswordResetToken(User user) {
    return Jwts.builder()
               .setSubject(user.getUsername())
               .setIssuedAt(new Date())
               .setExpiration(new Date((new Date()).getTime() + resetExpiration))
               .signWith(SignatureAlgorithm.HS256, jwtSecret)
               .compact();
  }

  public String getUsernameFromJwtToken(String token) {
    return Jwts.parser()
               .setSigningKey(jwtSecret)
               .parseClaimsJws(token)
               .getBody()
               .getSubject();
  }

  public Message validateJwtToken(String authToken) {
    try {
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      Date parsedToken = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody().getExpiration();
      ZoneId defaultZoneId = ZoneId.systemDefault();
      Instant instant = parsedToken.toInstant();
      LocalDateTime localDateTime = instant.atZone(defaultZoneId).toLocalDateTime();
      return new Message(true);
    } catch (SignatureException e) {
      logger.error("Invalid JWT signature -> Message: {} ", e);
      return new Message(e.toString(), false);
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token -> Message: {}", e);
      return new Message(e.toString(), false);
    } catch (ExpiredJwtException e) {
      logger.error("Expired JWT token -> Message: {}", e.getMessage());
      throw new TokenExpiredException("Token expired");
    } catch (UnsupportedJwtException e) {
      logger.error("Unsupported JWT token -> Message: {}", e);
      return new Message(e.toString(), false);
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty -> Message: {}", e);
      return new Message(e.toString(), false);
    }
  }

}
