package at.undok.it.clienttests;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.auth.serviceimpl.RoleService;
import at.undok.common.message.Message;
import at.undok.it.IntegrationTestBase;
import at.undok.it.cucumber.UndokTestData;
import at.undok.it.cucumber.auth.*;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.form.ClientForm;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientIntegrationTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthRestApiClient authRestApiClient;

    @Autowired
    private EmailVerifications emailVerifications;

    @Autowired
    private UserVerifications userVerifications;

    @Autowired
    private RoleService roleService;

    @LocalServerPort
    private int port;

    private static final String HOST = "http://localhost:";

    private String accessToken;

    @BeforeAll
    public void setAccessToken() {
        generateJwtToken();
    }

    @Test
    public void testMist() {
        String forObject = testRestTemplate.getForObject(HOST + port + "/service/auth/mist", String.class);
        assertEquals("Hi du verdammter Mistkerl", forObject);
    }

    @Test
    public void testCreateOnlyOneClient() {
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword("test_the_shit");
        ResponseEntity<Client> clientResponseEntity = authRestApiClient.createClient(clientForm, accessToken);
        assertTrue(clientResponseEntity.getStatusCode().is2xxSuccessful(), "fick dich");
    }

    @Test
    public void testCreateMoreClients() {

    }

    private void generateJwtToken() {
        UserDto validRandomUserDto = setValidRandomUser();
        String secFacToken = getSecFacToken(validRandomUserDto.getEmail());
        SecondFactorForm secondFactorForm = new SecondFactorForm(validRandomUserDto.getId(), secFacToken);
        ResponseEntity<JwtResponse> jwtResponseResponseEntity = authRestApiClient.secFac(secondFactorForm, validRandomUserDto.getAccessToken());
        accessToken = Objects.requireNonNull(jwtResponseResponseEntity.getBody()).getUserDto().getAccessToken();
    }

    private UserDto setValidRandomUser() {
        UndokTestData undokTestData = new UndokTestData(new Faker());
        SignUpDto validRandomSignUpDto = undokTestData.newRegistration();
        registerRandomUser(validRandomSignUpDto);
        confirmRandomUser(validRandomSignUpDto.getUsername(), validRandomSignUpDto.getEmail());
        ResponseEntity<JwtResponse> login = authRestApiClient.login(new LoginDto(validRandomSignUpDto.getUsername(), null, validRandomSignUpDto.getPassword()));
        return Objects.requireNonNull(login.getBody()).getUserDto();
    }

    private void registerRandomUser(SignUpDto validRandomSignUpDto) {
        ResponseEntity<Message> messageResponseEntity = authRestApiClient.registerUser(validRandomSignUpDto);
        log.debug("Random user has username {} and email {}", validRandomSignUpDto.getUsername(), validRandomSignUpDto.getEmail());
        assertThat(messageResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private void confirmRandomUser(String username, String email) {
        var emailMessage = emailVerifications.assertEmailSentTo(email, EmailType.CONFIRMATION);
        var content = emailVerifications.getEmailContent(emailMessage);
        var confirmationLink = emailVerifications.parseConfirmationLink(content, "a.confirmation");
        theConfirmationLink(confirmationLink);
        var expected = UserConfirmationStatus.valueOf("confirmed".toUpperCase());
        userVerifications.assertUserConfirmationStatus(username, expected);
    }

    private void theConfirmationLink(String confirmationLink) {
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(confirmationLink, Void.class);
        log.debug("Confirmation request returned with a {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String getSecFacToken(String email) {
        var secondFactorEmailMessage = emailVerifications.assertEmailSentTo(email, EmailType.SECOND_FACTOR);
        var secFacContent = emailVerifications.getEmailContent(secondFactorEmailMessage);
        return emailVerifications.parseConfirmationLink(secFacContent, "div.general");
    }
}
