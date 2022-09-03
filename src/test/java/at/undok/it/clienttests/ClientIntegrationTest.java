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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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

    @Value("${service.b.org.app.jwtSecret}")
    private String jwtSecret;

    private SignUpDto validRandomSignUpDto;

    private UserDto setValidRandomUser() {
        UndokTestData undokTestData = new UndokTestData(new Faker());
        validRandomSignUpDto = undokTestData.newRegistration();
        ResponseEntity<Message> messageResponseEntity = authRestApiClient.registerUser(validRandomSignUpDto);
        log.debug("Random user has username {} and email {}", validRandomSignUpDto.getUsername(), validRandomSignUpDto.getEmail());
        var emailMessage = emailVerifications.assertEmailSentTo(validRandomSignUpDto.getEmail(), EmailType.CONFIRMATION);
        var content = emailVerifications.getEmailContent(emailMessage);
        var confirmationLink = emailVerifications.parseConfirmationLink(content, "a.confirmation", "href");
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(confirmationLink, Void.class);
        var expected = UserConfirmationStatus.valueOf("confirmed".toUpperCase());
        userVerifications.assertUserConfirmationStatus(validRandomSignUpDto.getUsername(), expected);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(validRandomSignUpDto.getUsername());
        loginDto.setPassword(validRandomSignUpDto.getPassword());
        ResponseEntity<JwtResponse> login = authRestApiClient.login(loginDto);
//        var secondFactorEmailMessage = emailVerifications.assertEmailSentTo(validRandomSignUpDto.getEmail(), EmailType.SECOND_FACTOR);
//        var secFacContent = emailVerifications.getEmailContent(secondFactorEmailMessage);
//        var secFacToken = emailVerifications.parseConfirmationLink(secFacContent, "div.general", "class");
        return Objects.requireNonNull(login.getBody()).getUserDto();
    }

    private String getSecFacToken() {
        var secondFactorEmailMessage = emailVerifications.assertEmailSentTo(validRandomSignUpDto.getEmail(), EmailType.SECOND_FACTOR);
        var secFacContent = emailVerifications.getEmailContent(secondFactorEmailMessage);
        return emailVerifications.parseConfirmationLink(secFacContent, "div.general", "class");
    }

    @Test
    public void testMist() {
        String forObject = testRestTemplate.getForObject(HOST + port + "/service/auth/mist", String.class);
        assertEquals("Hi du verdammter Mistkerl", forObject);
    }

    @Test
    public void testWithToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + generateJwtToken());
        String url = HOST + port + "/service/undok/clients/create";
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword("test_the_shit");
        HttpEntity<ClientForm> clientFormHttpEntity = new HttpEntity<>(clientForm, httpHeaders);
        ResponseEntity<Client> clientResponseEntity = this.testRestTemplate.postForEntity(url, clientFormHttpEntity, Client.class);
        assertTrue(clientResponseEntity.getStatusCode().is2xxSuccessful(), "fick dich");
    }

    private String generateJwtToken() {
        UserDto validRandomUserDto = setValidRandomUser();
        String secFacToken = getSecFacToken();
        // User validRandomUserEntity =
        SecondFactorForm secondFactorForm = new SecondFactorForm(validRandomUserDto.getId(), secFacToken);
        ResponseEntity<JwtResponse> jwtResponseResponseEntity = authRestApiClient.secFac(secondFactorForm, validRandomUserDto.getAccessToken());
//        String authorities = "ROLE_USER";
//        return Jwts.builder()
//                   .setSubject((validRandomUser.getUsername()))
//                   .setIssuedAt(new Date())
//                   .setExpiration(new Date((new Date()).getTime() + 300000))
//                   .claim("roles", authorities)
//                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                   .compact();
        return Objects.requireNonNull(jwtResponseResponseEntity.getBody()).getUserDto().getAccessToken();
    }
}
