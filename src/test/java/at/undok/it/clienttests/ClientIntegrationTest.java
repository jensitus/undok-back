package at.undok.it.clienttests;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import at.undok.it.IntegrationTestBase;
import at.undok.it.cucumber.UndokTestData;
import at.undok.it.cucumber.auth.*;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientIntegrationTest extends IntegrationTestBase {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthRestApiClient authRestApiClient;

    @Autowired
    private EmailVerifications emailVerifications;

    @Autowired
    private UserVerifications userVerifications;

    private String accessToken;

    private UUID clientId;

    @BeforeAll
    public void setAccessToken() {
        generateJwtAccessToken();
    }


    @Test
    public void testCreateOnlyOneClient() {
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword("test_the_shit");
        ResponseEntity<Client> clientResponseEntity = authRestApiClient.createClient(clientForm, accessToken);
        assertTrue(clientResponseEntity.getStatusCode().is2xxSuccessful(), "super");
    }

    @Test
    public void testCreateMoreClients() {
        ClientForm secondClientForm = createClientForm("second_client");
        ClientForm thirdClientForm = createClientForm("third_client");
        ResponseEntity<Client> secondClient = authRestApiClient.createClient(secondClientForm, accessToken);
        clientId = Objects.requireNonNull(secondClient.getBody()).getPerson().getId();
        ResponseEntity<Client> thirdClient = authRestApiClient.createClient(thirdClientForm, accessToken);
        CounselingForm counselingFormSecondClient = createCounselingForm(Objects.requireNonNull(secondClient.getBody()).getId());
        CounselingForm counselingFormThirdClient = createCounselingForm(Objects.requireNonNull(Objects.requireNonNull(thirdClient.getBody()).getId()));
        ResponseEntity<CounselingDto> counseling_02 = authRestApiClient.createCounseling(counselingFormSecondClient,
                accessToken, secondClient.getBody().getId());
        ResponseEntity<CounselingDto> counseling_03 = authRestApiClient.createCounseling(counselingFormThirdClient,
                accessToken, thirdClient.getBody().getId());
        getCounselings();
    }

    @Test
    public void testDeleteClient() {
        int first = checkDeletedClient();
        authRestApiClient.getClient(clientId, accessToken);
        authRestApiClient.deleteClient(clientId, accessToken);
        int second = checkDeletedClient();
        int sum = first - second;
        assertEquals(1, sum);
        List<CounselingDto> counselings = getCounselings();
        
    }


    private int checkDeletedClient() {
        List<AllClientDto> allClients = authRestApiClient.getAllClients(accessToken);
        assertNotNull(allClients);
        return allClients.size();
    }

    private List<CounselingDto> getCounselings() {
        ResponseEntity<List<CounselingDto>> allCounselings = authRestApiClient.getAllCounselings(accessToken);
        List<CounselingDto> counselingDtos = allCounselings.getBody();
        assertNotNull(counselingDtos);
        return counselingDtos;
    }

    private ClientForm createClientForm(String keyword) {
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword(keyword);
        return clientForm;
    }

    private CounselingForm createCounselingForm(UUID clientId) {
        CounselingForm counselingForm = new CounselingForm();
        counselingForm.setClientId(Objects.requireNonNull(clientId));
        counselingForm.setCounselingDate("09-09-2022 11:00");
        return counselingForm;
    }

    private void generateJwtAccessToken() {
        UserDto validRandomUserDto = setValidRandomUser();
        String secFacToken = getSecondFactorToken(validRandomUserDto.getEmail());
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

    private String getSecondFactorToken(String email) {
        var secondFactorEmailMessage = emailVerifications.assertEmailSentTo(email, EmailType.SECOND_FACTOR);
        var secFacContent = emailVerifications.getEmailContent(secondFactorEmailMessage);
        return emailVerifications.parseConfirmationLink(secFacContent, "div.general");
    }
}
