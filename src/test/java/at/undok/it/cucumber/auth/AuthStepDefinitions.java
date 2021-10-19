package at.undok.it.cucumber.auth;

import at.undok.auth.model.dto.SignUpDto;
import at.undok.common.message.Message;
import at.undok.it.cucumber.UndokTestData;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthStepDefinitions {

    private final AuthRestApiClient authRestApiClient;
    private final UndokTestData undokTestData;
    private final Map<String, ResponseEntity<Message>> registrationResponses = new TreeMap<>();

    private SignUpDto validRandomUser;

    static GreenMailExtension greenMail = new GreenMailExtension();

    public AuthStepDefinitions(AuthRestApiClient authRestApiClient, UndokTestData undokTestData) {
        this.authRestApiClient = authRestApiClient;
        this.undokTestData = undokTestData;
    }

    @Before
    public void before() {
        greenMail.beforeEach(null);
    }


    @After
    public void after() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        greenMail.afterEach(null);
        registrationResponses.clear();
    }

    @Given("new valid random user")
    public void newValidRandomUser() {
        validRandomUser = undokTestData.newRegistration();
    }

    @When("trying to register this user")
    public void tryingToRegisterThisUser() {
        var response = authRestApiClient.registerUser(validRandomUser);
        assertResponseWithBody(response);

        registrationResponses.put(validRandomUser.getUsername(), response);
    }

    @And("an confirmation email to be sent")
    public void anConfirmationEmailToBeSent() {
        var messages = greenMail.getReceivedMessages();
        assertThat(messages.length).isNotZero();
    }

    @Given("existing user with username {string}")
    @When("another user tries to register with username {string}")
    public void existingUserWithUsername(String username) {
        validRandomUser = undokTestData.newRegistration();
        validRandomUser.setUsername(username);

        var response = authRestApiClient.registerUser(validRandomUser);
        assertResponseWithBody(response);

        registrationResponses.put(username, response);
    }

    @Then("expect a {string} registration response with message {string}")
    public void expectAResponseWithMessage(String expectedHttpStatusCodeWithName, String expectedErrorMessage) {
        var response = registrationResponses
                .values()
                .stream()
                .reduce((r1, r2) -> r2)
                .orElseThrow(() -> new IllegalStateException("There should be at least 1 registration response present"));

        var expectedStatusCode = parseExpectedHttpStatusCodeWithName(expectedHttpStatusCodeWithName);

        var actualStatusCode = response.getStatusCode();
        var actualMessage = response.getBody();

        assertThat(actualStatusCode).isEqualTo(expectedStatusCode);
        assertThat(actualMessage).hasFieldOrPropertyWithValue("text", expectedErrorMessage);
    }

    private HttpStatus parseExpectedHttpStatusCodeWithName(String expectedHttpStatusCodeWithName) {
        var split = expectedHttpStatusCodeWithName.split(" ");
        if (split.length != 2) {
            throw new IllegalStateException(String.format("Http Status Codes must be in the format '<code> <name>' eg. '409 CONFLICT'. Currently, '%s' was given", expectedHttpStatusCodeWithName));
        }

        return HttpStatus.valueOf(split[1]);
    }

    private void assertResponseWithBody(ResponseEntity<Message> response) {
        assertThat(response)
                .isNotNull()
                .hasFieldOrProperty("body");
    }
}
