package at.undok.it.clienttests;

import at.undok.auth.model.entity.RoleName;
import at.undok.common.message.Message;
import at.undok.it.IntegrationTestBase;
import at.undok.it.clienttests.utils.TokenUtils;
import at.undok.it.cucumber.auth.AuthRestApiClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestIT extends IntegrationTestBase {

    @Autowired
    private AuthRestApiClient authRestApiClient;

    @Test
    public void firstTest() {
        String token = generateAccessToken("admin",
                                           EnumSet.of(RoleName.ROLE_SECOND_FACTOR, RoleName.ROLE_ADMIN),
                                           Collections.emptyList());
        log.info(token);
        ResponseEntity<Message> response = authRestApiClient.pingPong(token);
        assertEquals(Objects.requireNonNull(response.getBody()).getText(), "ping pong");
        log.info(" t e s t + + + + + +");
    }

    @SneakyThrows
    protected String generateAccessToken(String userId, Collection<RoleName> authorities, Collection<String> tenantNames) {
        Map<String, Object> claims = new HashMap<>();
        if (userId != null) {
            claims.put("user_name", userId);
        }
        if (authorities != null) {
            claims.put("roles", authorities.stream()
                                           .map(RoleName::name)
                                           .collect(Collectors.toList()));
        }
        if (tenantNames != null) {
            claims.put("tenants", tenantNames);
        }
        return TokenUtils.jwtTokenBuilder().claims(claims).build();
    }

}
