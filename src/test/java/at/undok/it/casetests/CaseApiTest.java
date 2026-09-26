package at.undok.it.casetests;

import at.undok.common.message.Message;
import at.undok.it.IntegrationTestBase;
import at.undok.it.auth.TestUserAuthenticator;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CloseCaseForm;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.it.cucumber.auth.AuthRestApiClient;
import at.undok.undok.client.util.StatusService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The close and reopen endpoints over HTTP: routing, the security annotation, and the JSON
 * binding of CloseCaseForm — none of which CloseCaseTest and ReopenCaseTest exercise, since
 * they call the service directly.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PUT /service/undok/case/{id}/close and /reopen")
public class CaseApiTest extends IntegrationTestBase {

    private static final String HOST = "http://localhost:";
    private static final int SERVER_PORT = 1200;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthRestApiClient authRestApiClient;

    @Autowired
    private TestUserAuthenticator testUserAuthenticator;

    private String accessToken;

    @BeforeAll
    public void authenticate() {
        accessToken = testUserAuthenticator.newAuthenticatedUserToken();
    }

    /**
     * A case is only created as a side effect of logging the first counseling, so the fixture
     * has to go through that path rather than posting a case directly.
     */
    private ClientDto givenClientWithOpenCase() {
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword("close-case-" + UUID.randomUUID());
        ResponseEntity<ClientDto> created = authRestApiClient.createClient(clientForm, accessToken);
        UUID clientId = Objects.requireNonNull(created.getBody()).getId();

        CounselingForm counselingForm = new CounselingForm();
        counselingForm.setClientId(clientId);
        counselingForm.setCounselingDate("09-09-2022 11:00");
        authRestApiClient.createCounseling(counselingForm, accessToken, clientId);

        ClientDto client = authRestApiClient.getClient(clientId, accessToken);
        assertThat(client.getOpenCase()).isNotNull();
        return client;
    }

    private <T> ResponseEntity<T> close(UUID caseId, CloseCaseForm form, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        String url = HOST + SERVER_PORT + "/service/undok/case/" + caseId + "/close";
        return testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(form, headers), responseType);
    }

    private <T> ResponseEntity<T> reopen(UUID caseId, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        String url = HOST + SERVER_PORT + "/service/undok/case/" + caseId + "/reopen";
        return testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(headers), responseType);
    }

    private CloseCaseForm form(LocalDate endDate, String referredTo) {
        CloseCaseForm form = new CloseCaseForm();
        form.setEndDate(endDate);
        form.setReferredTo(referredTo);
        return form;
    }

    @Test
    @DisplayName("closes the case and binds the ISO end date the frontend sends")
    void shouldCloseOverHttp() {
        ClientDto client = givenClientWithOpenCase();
        // The fixture's case starts today, so today is the only end date it can legally take.
        LocalDate endDate = LocalDate.now();

        ResponseEntity<CaseDto> response = close(client.getOpenCase().getId(),
                                                 form(endDate, "AK Wien"), CaseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CaseDto closed = Objects.requireNonNull(response.getBody());
        assertThat(closed.getStatus()).isEqualTo(StatusService.STATUS_CLOSED);
        assertThat(closed.getEndDate()).isEqualTo(endDate);
        assertThat(closed.getReferredTo()).isEqualTo("AK Wien");

        // and the client detail endpoint now serves it as a closed case, not an open one
        ClientDto reloaded = authRestApiClient.getClient(client.getId(), accessToken);
        assertThat(reloaded.getOpenCase()).isNull();
        assertThat(reloaded.getClosedCases()).hasSize(1);
    }

    @Test
    @DisplayName("a second close comes back as 409, not a silent re-stamp")
    void shouldRejectSecondClose() {
        ClientDto client = givenClientWithOpenCase();
        UUID caseId = client.getOpenCase().getId();
        close(caseId, form(LocalDate.now(), "AK Wien"), CaseDto.class);

        ResponseEntity<Message> response = close(caseId, form(LocalDate.now(), "ÖGB"), Message.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Objects.requireNonNull(response.getBody()).getText())
                .isEqualTo("Dieser Fall ist bereits abgeschlossen.");
    }

    @Test
    @DisplayName("a future end date comes back as 422 with a message the UI can show")
    void shouldRejectFutureEndDate() {
        ClientDto client = givenClientWithOpenCase();

        ResponseEntity<Message> response = close(client.getOpenCase().getId(),
                                                 form(LocalDate.now().plusDays(1), null), Message.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(Objects.requireNonNull(response.getBody()).getText())
                .isEqualTo("Das Enddatum darf nicht in der Zukunft liegen.");
    }

    @Test
    @DisplayName("an end date before the case's start date comes back as 422")
    void shouldRejectEndDateBeforeStart() {
        ClientDto client = givenClientWithOpenCase();

        ResponseEntity<Message> response = close(client.getOpenCase().getId(),
                                                 form(LocalDate.now().minusDays(3), null), Message.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(Objects.requireNonNull(response.getBody()).getText())
                .isEqualTo("Das Enddatum darf nicht vor dem Startdatum liegen.");
    }

    @Test
    @DisplayName("an omitted end date falls back to today rather than failing to bind")
    void shouldAcceptOmittedEndDate() {
        ClientDto client = givenClientWithOpenCase();

        ResponseEntity<CaseDto> response = close(client.getOpenCase().getId(),
                                                 form(null, null), CaseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getEndDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("reopens the case and serves it as the open one again")
    void shouldReopenTheCase() {
        ClientDto client = givenClientWithOpenCase();
        UUID caseId = client.getOpenCase().getId();
        close(caseId, form(LocalDate.now(), "AK Wien"), CaseDto.class);

        ResponseEntity<CaseDto> response = reopen(caseId, CaseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CaseDto reopened = Objects.requireNonNull(response.getBody());
        assertThat(reopened.getStatus()).isEqualTo(StatusService.STATUS_OPEN);
        assertThat(reopened.getEndDate()).isNull();
        assertThat(reopened.getReferredTo()).isNull();

        ClientDto reloaded = authRestApiClient.getClient(client.getId(), accessToken);
        assertThat(reloaded.getOpenCase()).isNotNull();
        assertThat(reloaded.getOpenCase().getId()).isEqualTo(caseId);
        assertThat(reloaded.getClosedCases()).isNullOrEmpty();
    }

    @Test
    @DisplayName("reopening a case that is already open comes back as 409 with a showable message")
    void shouldRejectReopenOfOpenCase() {
        ClientDto client = givenClientWithOpenCase();

        ResponseEntity<Message> response = reopen(client.getOpenCase().getId(), Message.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(Objects.requireNonNull(response.getBody()).getText())
                .isEqualTo("Dieser Fall ist nicht abgeschlossen.");
    }

    @Test
    @DisplayName("the reopen endpoint is not reachable without a token")
    void shouldRequireAuthenticationForReopen() {
        ClientDto client = givenClientWithOpenCase();
        String url = HOST + SERVER_PORT + "/service/undok/case/" + client.getOpenCase().getId() + "/reopen";

        ResponseEntity<String> response = testRestTemplate.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(new HttpHeaders()), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isFalse();
    }

    @Test
    @DisplayName("the endpoint is not reachable without a token")
    void shouldRequireAuthentication() {
        ClientDto client = givenClientWithOpenCase();
        String url = HOST + SERVER_PORT + "/service/undok/case/" + client.getOpenCase().getId() + "/close";

        ResponseEntity<String> response = testRestTemplate.exchange(
                url, HttpMethod.PUT, new HttpEntity<>(form(null, null), new HttpHeaders()), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isFalse();
    }

}
