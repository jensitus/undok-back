package at.undok.it.cucumber.auth;

import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.common.message.Message;
import at.undok.it.cucumber.UndokClientTestData;
import at.undok.it.cucumber.UndokTestData;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.form.ClientForm;
import com.icegreen.greenmail.spring.GreenMailBean;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthStepDefinitions {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthStepDefinitions.class);

    private final AuthRestApiClient authRestApiClient;
    private final GreenMailBean greenMail;
    private final EmailVerifications emailVerifications;
    private final UserVerifications userVerifications;
    private final HttpVerifications httpVerifications;
    private final UndokTestData undokTestData;
    private final Map<String, ResponseEntity<Message>> registrationResponses = new TreeMap<>();
    private final ClientRestApiClient clientRestApiClient;
    private final UndokClientTestData undokClientTestData;

    private SignUpDto validRandomUser;
    private String confirmationLink;
    private String validRandomUserAccessToken;
    private ClientForm validClient;
    private ClientDto validCreatedClientDto;


    public AuthStepDefinitions(AuthRestApiClient authRestApiClient, GreenMailBean greenMail, EmailVerifications emailVerifications, UserVerifications userVerifications, HttpVerifications httpVerifications, UndokTestData undokTestData, ClientRestApiClient clientRestApiClient, UndokClientTestData undokClientTestData) {
        this.authRestApiClient = authRestApiClient;
        this.greenMail = greenMail;
        this.emailVerifications = emailVerifications;
        this.userVerifications = userVerifications;
        this.httpVerifications = httpVerifications;
        this.undokTestData = undokTestData;
        this.clientRestApiClient = clientRestApiClient;
        this.undokClientTestData = undokClientTestData;
    }

    @Before
    public void before() {
        greenMail.start();
    }

    @After
    public void after() {
        greenMail.stop();
        registrationResponses.clear();
    }

    @Given("new valid random user")
    public void newValidRandomUser() {
        validRandomUser = undokTestData.newRegistration();
        LOGGER.debug("Random user has username {} and email {}", validRandomUser.getUsername(), validRandomUser.getEmail());
    }

    @Given("existing confirmed user")
    public void existingConfirmedUser() {
        newValidRandomUser();
        tryingToRegisterThisUser();
        expectAResponseWithMessage("201 CREATED", "user created");
        theUserToHaveConfirmationStatus("unconfirmed");
        anConfirmationEmailToBeSent();
        clickingTheConfirmationLink();
        theUserToHaveConfirmationStatus("confirmed");
    }

    @When("trying to register this user")
    public void tryingToRegisterThisUser() {
        LOGGER.debug("Registering user {} with undok", validRandomUser.getUsername());
        var response = authRestApiClient.registerUser(validRandomUser);
        httpVerifications.assertResponseWithBody(response);

        registrationResponses.put(validRandomUser.getUsername(), response);
    }

    @And("the user to have confirmation status {string}")
    public void theUserToHaveConfirmationStatus(String expectedConfirmationStatus) {
        var expected = UserConfirmationStatus.valueOf(expectedConfirmationStatus.toUpperCase());
        userVerifications.assertUserConfirmationStatus(validRandomUser.getUsername(), expected);
    }

    @And("an confirmation email to be sent")
    public void anConfirmationEmailToBeSent() {
        LOGGER.debug("Trying to retrieve E-Mails sent to {}", validRandomUser.getEmail());

        var emailMessage = emailVerifications.assertEmailSentTo(validRandomUser.getEmail(), EmailType.CONFIRMATION);
        var content = emailVerifications.getEmailContent(emailMessage);
        confirmationLink = emailVerifications.parseConfirmationLink(content);

        LOGGER.debug("Found confirmation link in E-Mail: {}", confirmationLink);
    }

    @When("clicking the confirmation link")
    public void clickingTheConfirmationLink() {
        LOGGER.debug("Invoking a GET request on confirmation Link: {}", confirmationLink);
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(confirmationLink, Void.class);
        LOGGER.debug("Confirmation request returned with a {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Given("existing user with username {string}")
    @When("another user tries to register with username {string}")
    public void existingUserWithUsername(String username) {
        validRandomUser = undokTestData.newRegistration();
        validRandomUser.setUsername(username);

        var response = authRestApiClient.registerUser(validRandomUser);
        httpVerifications.assertResponseWithBody(response);

        registrationResponses.put(username, response);
    }

    @Then("expect a {string} registration response with message {string}")
    public void expectAResponseWithMessage(String expectedHttpStatusCodeWithName, String expectedErrorMessage) {
        var response = registrationResponses
                .values()
                .stream()
                .reduce((r1, r2) -> r2)
                .orElseThrow(() -> new IllegalStateException("There should be at least 1 registration response present"));

        httpVerifications.verifyResponseWithMessage(response, expectedHttpStatusCodeWithName, expectedErrorMessage);
    }

    @When("trying to login")
    public void tryingToLogin() {
        var loginRequest = new LoginDto();
        loginRequest.setUsername(validRandomUser.getUsername());
        loginRequest.setPassword(validRandomUser.getPassword());

        var response = authRestApiClient.authenticateUser(loginRequest);

        httpVerifications.assertResponseWithBody(response);

        var body = response.getBody();
        validRandomUserAccessToken = body.getUserDto().getAccessToken();

        LOGGER.debug("Received access token {}", validRandomUserAccessToken);
    }

    @Then("expect api token to be returned")
    public void expectApiTokenToBeReturned() {
        assertThat(validRandomUserAccessToken).isNotBlank();
    }

    /* * * * * * * *
     * client feature:
     *
     */

    @Given("loggedIn User")
    public void loggedInUser() {
        existingConfirmedUser();
        tryingToLogin();
        expectApiTokenToBeReturned();
    }

    @When("trying to create a client")
    public void tryingToCreateAClient() {
        validClient = undokClientTestData.createClientForm();
        var response = clientRestApiClient.createClient(validClient);
        httpVerifications.assertResponseWithBody(response);
        validCreatedClientDto = response.getBody();
    }

    @Then("expect this client to be created")
    public void expectThisClientToBeCreated() {
        assertThat(validCreatedClientDto.getKeyword()).isNotBlank();
    }
}
