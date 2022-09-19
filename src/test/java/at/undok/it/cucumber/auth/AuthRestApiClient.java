package at.undok.it.cucumber.auth;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthRestApiClient {

    private static final String HOST = "http://localhost:";
    private static final String UNDOK_CLIENTS_PATH = "/service/undok/clients";
    private static final String UNDOK_COUNSELINGS_PATH = "/service/undok/counselings";
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

    public ResponseEntity<Client> createClient(ClientForm clientForm, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/create";
        HttpEntity<ClientForm> clientFormHttpEntity = new HttpEntity<>(clientForm, httpHeaders);
        return this.testRestTemplate.postForEntity(url, clientFormHttpEntity, Client.class);
    }

    public ResponseEntity<CounselingDto> createCounseling(CounselingForm counselingForm, String accessToken, UUID clientId) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/" + clientId + "/counseling";
        HttpEntity<CounselingForm> counselingFormHttpEntity = new HttpEntity<>(counselingForm, httpHeaders);
        return testRestTemplate.postForEntity(url, counselingFormHttpEntity, CounselingDto.class);
    }

    public ResponseEntity<List<CounselingDto>> getAllCounselings(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_COUNSELINGS_PATH + "/all";
        HttpEntity entity = new HttpEntity(httpHeaders);
        ResponseEntity<List<CounselingDto>> response = testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CounselingDto>>() {
        });

        return response;
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        return httpHeaders;
    }

}
