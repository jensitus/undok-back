package at.undok.it.cucumber.auth;

import at.undok.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpVerifications {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpVerifications.class);

    public void assertResponseWithBody(ResponseEntity<Message> response) {
        assertThat(response)
                .isNotNull()
                .hasFieldOrProperty("body");
    }

    public void verifyResponseWithMessage(ResponseEntity<Message> response, String expectedHttpStatusCodeWithName, String expectedErrorMessage) {
        var expectedStatusCode = parseExpectedHttpStatusCodeWithName(expectedHttpStatusCodeWithName);

        var actualStatusCode = response.getStatusCode();
        var actualMessage = response.getBody();

        LOGGER.debug("Got http response {}", actualStatusCode);

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
}
