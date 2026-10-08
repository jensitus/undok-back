package at.undok.it.auth;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import at.undok.it.cucumber.UndokTestData;
import at.undok.it.cucumber.auth.AuthRestApiClient;
import at.undok.it.cucumber.auth.EmailType;
import at.undok.it.cucumber.auth.EmailVerifications;
import at.undok.it.cucumber.auth.UserConfirmationStatus;
import at.undok.it.cucumber.auth.UserVerifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Registers, confirms and fully authenticates a throwaway user, so an API test can get at a
 * usable access token without repeating the register → confirm → login → second factor dance.
 */
public class TestUserAuthenticator {

    private final AuthRestApiClient authRestApiClient;
    private final EmailVerifications emailVerifications;
    private final UserVerifications userVerifications;
    private final UndokTestData testData;

    public TestUserAuthenticator(AuthRestApiClient authRestApiClient,
                                 EmailVerifications emailVerifications,
                                 UserVerifications userVerifications,
                                 UndokTestData testData) {
        this.authRestApiClient = authRestApiClient;
        this.emailVerifications = emailVerifications;
        this.userVerifications = userVerifications;
        this.testData = testData;
    }

    /**
     * @return an access token that has already cleared the second factor, so it carries ROLE_USER
     */
    public String newAuthenticatedUserToken() {
        SignUpDto signUpDto = testData.newRegistration();
        register(signUpDto);
        confirm(signUpDto.getUsername(), signUpDto.getEmail());

        ResponseEntity<JwtResponse> login = authRestApiClient.login(
                new LoginDto(signUpDto.getUsername(), null, signUpDto.getPassword()));
        UserDto userDto = Objects.requireNonNull(login.getBody()).getUserDto();

        String secondFactorToken = secondFactorToken(signUpDto.getEmail());
        ResponseEntity<JwtResponse> secondFactor = authRestApiClient.secFac(
                new SecondFactorForm(userDto.getId(), secondFactorToken), userDto.getAccessToken());

        return Objects.requireNonNull(secondFactor.getBody()).getUserDto().getAccessToken();
    }

    private void register(SignUpDto signUpDto) {
        ResponseEntity<Message> response = authRestApiClient.registerUser(signUpDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private void confirm(String username, String email) {
        var emailMessage = emailVerifications.assertEmailSentTo(email, EmailType.CONFIRMATION);
        var content = emailVerifications.getEmailContent(emailMessage);
        var confirmationLink = emailVerifications.parseConfirmationLink(content, "a.confirmation");

        var response = new RestTemplate().getForEntity(confirmationLink, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        userVerifications.assertUserConfirmationStatus(username, UserConfirmationStatus.CONFIRMED);
    }

    private String secondFactorToken(String email) {
        var message = emailVerifications.assertEmailSentTo(email, EmailType.SECOND_FACTOR);
        var content = emailVerifications.getEmailContent(message);
        return emailVerifications.parseConfirmationLink(content, "div.general");
    }

}
