package at.undok.it.casetests;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.exception.CaseAlreadyClosedException;
import at.undok.undok.client.exception.InvalidCaseEndDateException;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.ClientCaseProjection;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CloseCaseForm;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.service.CaseService;
import at.undok.undok.client.service.ClientService;
import at.undok.undok.client.util.StatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Closing a case, and the batch query that lets the clients list split open from closed.
 */
@DisplayName("Closing a case")
public class CloseCaseTest extends IntegrationTestBase {

    @Autowired
    private CaseService caseService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CaseRepo caseRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private CounselingRepo counselingRepo;

    private Client givenClient() {
        Client client = new Client();
        client.setKeyword("client-" + UUID.randomUUID());
        client.setStatus(StatusService.STATUS_ACTIVE);
        client.setCreatedAt(LocalDateTime.now());
        return clientRepo.saveAndFlush(client);
    }

    private Case givenCase(Client client, String status, LocalDate startDate, LocalDate endDate) {
        Case theCase = new Case();
        theCase.setName("case-" + UUID.randomUUID());
        theCase.setClientId(client.getId());
        theCase.setStatus(status);
        theCase.setStartDate(startDate);
        theCase.setEndDate(endDate);
        theCase.setCreatedAt(LocalDateTime.now());
        return caseRepo.saveAndFlush(theCase);
    }

    private Case givenOpenCase(Client client) {
        return givenCase(client, StatusService.STATUS_OPEN, LocalDate.now().minusMonths(2), null);
    }

    private void givenCounseling(Client client, Case theCase, int requiredTime) {
        Counseling counseling = new Counseling();
        counseling.setClient(client);
        counseling.setCounselingCase(theCase);
        counseling.setRequiredTime(requiredTime);
        counseling.setStatus(StatusService.STATUS_ACTIVE);
        counseling.setCreatedAt(LocalDateTime.now());
        counseling.setCounselingDate(LocalDateTime.now());
        counselingRepo.saveAndFlush(counseling);
    }

    private CloseCaseForm form(LocalDate endDate, String referredTo) {
        CloseCaseForm form = new CloseCaseForm();
        form.setEndDate(endDate);
        form.setReferredTo(referredTo);
        return form;
    }

    @Test
    @Transactional
    @DisplayName("stamps the end date the counsellor picked, not today")
    void shouldUseTheGivenEndDate() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);

        CaseDto closed = caseService.closeCase(openCase.getId(), form(lastWeek, "AK Wien"));

        assertThat(closed.getStatus()).isEqualTo(StatusService.STATUS_CLOSED);
        assertThat(closed.getEndDate()).isEqualTo(lastWeek);
        assertThat(closed.getReferredTo()).isEqualTo("AK Wien");
        assertThat(caseRepo.findById(openCase.getId()).orElseThrow().getEndDate()).isEqualTo(lastWeek);
    }

    @Test
    @Transactional
    @DisplayName("falls back to today when no end date is given")
    void shouldDefaultToToday() {
        Case openCase = givenOpenCase(givenClient());

        CaseDto closed = caseService.closeCase(openCase.getId(), form(null, null));

        assertThat(closed.getEndDate()).isEqualTo(LocalDate.now());
        assertThat(closed.getReferredTo()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("freezes the total consultation time summed over the case's counselings")
    void shouldFreezeTotalConsultationTime() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        givenCounseling(client, openCase, 30);
        givenCounseling(client, openCase, 45);

        CaseDto closed = caseService.closeCase(openCase.getId(), form(null, null));

        assertThat(closed.getTotalConsultationTime()).isEqualTo(75);
    }

    @Test
    @Transactional
    @DisplayName("a case without counselings closes with a null total, not a crash")
    void shouldCloseWithoutCounselings() {
        Case openCase = givenOpenCase(givenClient());

        CaseDto closed = caseService.closeCase(openCase.getId(), form(null, null));

        assertThat(closed.getStatus()).isEqualTo(StatusService.STATUS_CLOSED);
        assertThat(closed.getTotalConsultationTime()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("rejects an end date in the future")
    void shouldRejectFutureEndDate() {
        Case openCase = givenOpenCase(givenClient());

        assertThatThrownBy(() -> caseService.closeCase(openCase.getId(), form(LocalDate.now().plusDays(1), null)))
                .isInstanceOf(InvalidCaseEndDateException.class);
        assertThat(caseRepo.findById(openCase.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusService.STATUS_OPEN);
    }

    @Test
    @Transactional
    @DisplayName("rejects an end date before the start date")
    void shouldRejectEndDateBeforeStart() {
        Client client = givenClient();
        Case openCase = givenCase(client, StatusService.STATUS_OPEN, LocalDate.now().minusDays(5), null);

        assertThatThrownBy(() -> caseService.closeCase(openCase.getId(), form(LocalDate.now().minusDays(10), null)))
                .isInstanceOf(InvalidCaseEndDateException.class);
    }

    @Test
    @Transactional
    @DisplayName("refuses to close an already closed case instead of silently re-stamping it")
    void shouldRefuseSecondClose() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        LocalDate firstEnd = LocalDate.now().minusDays(3);
        caseService.closeCase(openCase.getId(), form(firstEnd, "AK Wien"));

        assertThatThrownBy(() -> caseService.closeCase(openCase.getId(), form(LocalDate.now(), "ÖGB")))
                .isInstanceOf(CaseAlreadyClosedException.class);
        Case reloaded = caseRepo.findById(openCase.getId()).orElseThrow();
        assertThat(reloaded.getEndDate()).isEqualTo(firstEnd);
        assertThat(reloaded.getReferredTo()).isEqualTo("AK Wien");
    }

    @Test
    @Transactional
    @DisplayName("the batch query prefers the open case over any closed one")
    void shouldPreferOpenCase() {
        Client client = givenClient();
        givenCase(client, StatusService.STATUS_CLOSED, LocalDate.now().minusYears(2), LocalDate.now().minusYears(1));
        Case openCase = givenOpenCase(client);

        Map<UUID, ClientCaseProjection> result = caseService.getCurrentCaseByClient(List.of(client.getId()));

        // Every projection getter must resolve — a mismatched column alias silently yields null.
        assertThat(result.get(client.getId())).satisfies(projection -> {
            assertThat(projection.getCaseId()).isEqualTo(openCase.getId());
            assertThat(projection.getClientId()).isEqualTo(client.getId());
            assertThat(projection.getStatus()).isEqualTo(StatusService.STATUS_OPEN);
            assertThat(projection.getStartDate()).isEqualTo(openCase.getStartDate());
            assertThat(projection.getEndDate()).isNull();
        });
    }

    @Test
    @Transactional
    @DisplayName("with only closed cases the batch query returns the most recently closed one")
    void shouldReturnMostRecentlyClosedCase() {
        Client client = givenClient();
        givenCase(client, StatusService.STATUS_CLOSED, LocalDate.now().minusYears(3), LocalDate.now().minusYears(2));
        Case recent = givenCase(client, StatusService.STATUS_CLOSED,
                                LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(1));

        Map<UUID, ClientCaseProjection> result = caseService.getCurrentCaseByClient(List.of(client.getId()));

        assertThat(result.get(client.getId()).getCaseId()).isEqualTo(recent.getId());
        assertThat(result.get(client.getId()).getEndDate()).isEqualTo(recent.getEndDate());
    }

    @Test
    @Transactional
    @DisplayName("a case with no counseling is still visible — the gap countOpenCases has")
    void shouldSeeCaseWithoutCounseling() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);

        assertThat(caseService.countOpenCases(client.getId())).isFalse();
        assertThat(caseService.getCurrentCaseByClient(List.of(client.getId())))
                .containsOnlyKeys(client.getId())
                .extractingByKey(client.getId())
                .extracting(ClientCaseProjection::getCaseId)
                .isEqualTo(openCase.getId());
    }

    @Test
    @Transactional
    @DisplayName("a client with no case at all is simply absent from the map")
    void shouldOmitCaselessClient() {
        Client caseless = givenClient();

        assertThat(caseService.getCurrentCaseByClient(List.of(caseless.getId()))).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("an empty client list short-circuits instead of building an invalid IN ()")
    void shouldShortCircuitOnEmptyInput() {
        assertThat(caseService.getCurrentCaseByClient(List.of())).isEmpty();
        assertThat(caseService.getCurrentCaseByClient(null)).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("closed cases come back oldest first, so the last one is the most recent")
    void shouldOrderClosedCasesOldestFirst() {
        Client client = givenClient();
        Case oldest = givenCase(client, StatusService.STATUS_CLOSED,
                                LocalDate.now().minusYears(3), LocalDate.now().minusYears(2));
        Case newest = givenCase(client, StatusService.STATUS_CLOSED,
                                LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(1));
        Case middle = givenCase(client, StatusService.STATUS_CLOSED,
                                LocalDate.now().minusYears(2), LocalDate.now().minusMonths(18));

        List<CaseDto> closed = caseService.getCaseByClientIdAndStatus(
                client.getId(), StatusService.STATUS_CLOSED);

        // The client detail page and the edit form both take the last element as "most recent".
        assertThat(closed).extracting(CaseDto::getId)
                          .containsExactly(oldest.getId(), middle.getId(), newest.getId());
    }

    @Test
    @Transactional
    @DisplayName("a client whose case is closed can still be edited — it used to NPE")
    void shouldEditClientWithClosedCase() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        caseService.closeCase(openCase.getId(), form(null, null));

        // What the frontend sends: the form reads client.openCase, so with the case closed
        // every case-scoped field comes up blank.
        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword(client.getKeyword());
        clientForm.setEducation("Pflichtschule");

        clientService.updateClient(client.getId(), clientForm);

        assertThat(clientRepo.findById(client.getId()).orElseThrow().getEducation())
                .isEqualTo("Pflichtschule");
    }

    @Test
    @Transactional
    @DisplayName("editing a client with a closed case leaves that case's data alone")
    void shouldNotTouchClosedCaseOnEdit() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        caseService.closeCase(openCase.getId(), form(LocalDate.now(), "AK Wien"));

        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword(client.getKeyword());
        clientForm.setWorkingRelationship("etwas anderes");
        clientForm.setTargetGroup("eine andere Zielgruppe");

        clientService.updateClient(client.getId(), clientForm);

        Case reloaded = caseRepo.findById(openCase.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(StatusService.STATUS_CLOSED);
        assertThat(reloaded.getReferredTo()).isEqualTo("AK Wien");
        assertThat(reloaded.getWorkingRelationship()).isNull();
        assertThat(reloaded.getTargetGroup()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("a client with an open case still gets its case fields written on edit")
    void shouldStillUpdateOpenCaseOnEdit() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);

        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword(client.getKeyword());
        clientForm.setWorkingRelationship("Angestellt");
        clientForm.setTargetGroup("Zielgruppe A");

        clientService.updateClient(client.getId(), clientForm);

        Case reloaded = caseRepo.findById(openCase.getId()).orElseThrow();
        assertThat(reloaded.getWorkingRelationship()).isEqualTo("Angestellt");
        assertThat(reloaded.getTargetGroup()).isEqualTo("Zielgruppe A");
    }

    @Test
    @Transactional
    @DisplayName("the /all list endpoint carries the case status through to AllClientDto")
    void getAllShouldPopulateCaseStatus() {
        Client client = givenClient();
        Case openCase = givenOpenCase(client);
        caseService.closeCase(openCase.getId(), form(LocalDate.now().minusDays(2), "AK Wien"));

        AllClientDto listed = clientService.getAll().stream()
                                           .filter(dto -> dto.getId().equals(client.getId()))
                                           .findFirst()
                                           .orElseThrow();

        assertThat(listed.getCaseId()).isEqualTo(openCase.getId());
        assertThat(listed.getCaseStatus()).isEqualTo(StatusService.STATUS_CLOSED);
        assertThat(listed.getCaseEndDate()).isEqualTo(LocalDate.now().minusDays(2));
        assertThat(listed.getReferredTo()).isEqualTo("AK Wien");
    }

    @Test
    @Transactional
    @DisplayName("a caseless client reaches the list with a null case status")
    void getAllShouldLeaveCaseStatusNullForCaselessClient() {
        Client caseless = givenClient();

        AllClientDto listed = clientService.getAll().stream()
                                           .filter(dto -> dto.getId().equals(caseless.getId()))
                                           .findFirst()
                                           .orElseThrow();

        assertThat(listed.getCaseStatus()).isNull();
        assertThat(listed.getCaseId()).isNull();
    }
}
