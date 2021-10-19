package at.undok.it.cucumber.auth;

import at.undok.auth.model.dto.SignUpDto;
import at.undok.common.message.Message;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class AuthRestApiClient {

    private final TestRestTemplate testRestTemplate;
    private final Integer serverPort;

    public AuthRestApiClient(TestRestTemplate testRestTemplate, int serverPort) {
        this.testRestTemplate = testRestTemplate;
        this.serverPort = serverPort;
    }

    public ResponseEntity<Message> registerUser(SignUpDto signUpDto) {
        return testRestTemplate.postForEntity(
                "http://localhost:{port}/service/auth/signup",
                signUpDto,
                Message.class,
                Map.of("port", serverPort)
        );
    }
}
