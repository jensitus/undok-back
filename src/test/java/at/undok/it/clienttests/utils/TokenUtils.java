package at.undok.it.clienttests.utils;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Singular;
import org.springframework.test.web.servlet.request.RequestPostProcessor;


public class TokenUtils {
    private static final String jwtSecret = "Pfm49mU!#xat6jXVfRRRD9";

    @Builder(builderClassName = "JwtTokenBuilder", builderMethodName = "jwtTokenBuilder")
    private static String generateToken(@Singular Map<String,Object> claims) {
        return Jwts.builder()
                   .setSubject("admin")
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + 60000))
                   .addClaims(claims)
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
//        Jwts.builder()
//            .setSubject((userPrincipal.getUsername()))
//            .setIssuedAt(new Date())
//            .setExpiration(new Date((new Date()).getTime() + expiration))
//            .claim("roles", authorities)
//            .signWith(SignatureAlgorithm.HS512, jwtSecret)
//            .compact();
    }


    public static RequestPostProcessor tokenAuthorization(Consumer<JwtTokenBuilder> customizer) {
        JwtTokenBuilder builder = jwtTokenBuilder();
        customizer.accept(builder);
        String token = builder.build();
        return (request) -> {
            request.addHeader("Authorization", "Bearer " + token);
            return request;
        };
    }
}
