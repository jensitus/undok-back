package at.undok.it.cucumber.auth;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class AuthRestApiClient {

    private static final String HOST = "http://localhost:";
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

    public ResponseEntity<JwtResponse> login(LoginDto loginDto) {
        String url = "http://localhost:{port}//service/auth/login";
        return testRestTemplate.postForEntity(url, loginDto, JwtResponse.class, Map.of("port", serverPort));
    }

    public ResponseEntity<JwtResponse> secFac(SecondFactorForm secondFactorForm, String token) {
        String url = HOST + serverPort + "/service/second-factor-auth/second-factor";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        HttpEntity<SecondFactorForm> secondFactorFormHttpEntity = new HttpEntity<>(secondFactorForm, httpHeaders);
        return testRestTemplate.postForEntity(url, secondFactorFormHttpEntity, JwtResponse.class);
    }
}
