package at.undok.it.cucumber.auth;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.form.ClientForm;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ClientRestApiClient {

    private final TestRestTemplate testRestTemplate;
    private final Integer serverPort;

    public ClientRestApiClient(TestRestTemplate testRestTemplate, int serverPort) {
        this.testRestTemplate = testRestTemplate;
        this.serverPort = serverPort;
    }

    public ResponseEntity<ClientDto> createClient(ClientForm clientForm) {
        ResponseEntity<ClientDto> clientDtoResponseEntity = testRestTemplate.postForEntity(
                "http://localhost:{port}/service/undok/clients/create",
                clientForm,
                ClientDto.class,
                Map.of("port", serverPort));
        return clientDtoResponseEntity;
    }

}
