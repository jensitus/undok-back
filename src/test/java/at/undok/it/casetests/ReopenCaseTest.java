package at.undok.it.casetests;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.exception.CaseReopenNotAllowedException;
import at.undok.undok.client.model.dto.CaseDto;
import at.undok.undok.client.model.dto.ClientDto;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Reopening a closed case, so its properties can be edited again.
 */
@DisplayName("Reopening a case")
public class ReopenCaseTest extends IntegrationTestBase {

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
    @DisplayName("undoes the close completely: end date, referral and frozen time are cleared")
    void shouldUndoTheClose() {
        Client client = givenClient();
        Case theCase = givenOpenCase(client);
        givenCounseling(client, theCase, 30);
        caseService.closeCase(theCase.getId(), form(LocalDate.now().minusDays(3), "AK Wien"));

        CaseDto reopened = caseService.reopenCase(theCase.getId());

        assertThat(reopened.getStatus()).isEqualTo(StatusService.STATUS_OPEN);
        assertThat(reopened.getEndDate()).isNull();
        assertThat(reopened.getReferredTo()).isNull();
        assertThat(reopened.getTotalConsultationTime()).isNull();

        Case reloaded = caseRepo.findById(theCase.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(StatusService.STATUS_OPEN);
        assertThat(reloaded.getEndDate()).isNull();
        assertThat(reloaded.getReferredTo()).isNull();
        assertThat(reloaded.getTotalConsultationTime()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("keeps the start date, so the case is not restarted")
    void shouldKeepTheStartDate() {
        Client client = givenClient();
        Case theCase = givenOpenCase(client);
        LocalDate startDate = theCase.getStartDate();
        caseService.closeCase(theCase.getId(), form(null, null));

        CaseDto reopened = caseService.reopenCase(theCase.getId());

        assertThat(reopened.getStartDate()).isEqualTo(startDate);
    }

    @Test
    @Transactional
    @DisplayName("refuses a case that is not closed")
    void shouldRefuseAnOpenCase() {
        Case openCase = givenOpenCase(givenClient());

        assertThatThrownBy(() -> caseService.reopenCase(openCase.getId()))
                .isInstanceOf(CaseReopenNotAllowedException.class)
                .hasMessageContaining("nicht abgeschlossen");
    }

    @Test
    @Transactional
    @DisplayName("refuses when the client already has an open case — two open cases break the edit form")
    void shouldRefuseWhenAnotherCaseIsOpen() {
        Client client = givenClient();
        Case closedCase = givenCase(client, StatusService.STATUS_CLOSED,
                                    LocalDate.now().minusYears(2), LocalDate.now().minusYears(1));
        givenOpenCase(client);

        assertThatThrownBy(() -> caseService.reopenCase(closedCase.getId()))
                .isInstanceOf(CaseReopenNotAllowedException.class)
                .hasMessageContaining("bereits einen offenen Fall");

        assertThat(caseRepo.findById(closedCase.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusService.STATUS_CLOSED);
    }

    @Test
    @Transactional
    @DisplayName("the reopened case becomes the client's openCase, so the detail page shows it")
    void shouldBecomeTheOpenCase() {
        Client client = givenClient();
        Case theCase = givenOpenCase(client);
        caseService.closeCase(theCase.getId(), form(null, "AK Wien"));

        caseService.reopenCase(theCase.getId());

        ClientDto dto = clientService.getClientById(client.getId());
        assertThat(dto.getOpenCase()).isNotNull();
        assertThat(dto.getOpenCase().getId()).isEqualTo(theCase.getId());
        // getClientById nulls the list rather than sending an empty one.
        assertThat(dto.getClosedCases()).isNullOrEmpty();
    }

    @Test
    @Transactional
    @DisplayName("case fields are writable again once the case is reopened")
    void shouldMakeCaseFieldsEditableAgain() {
        Client client = givenClient();
        Case theCase = givenOpenCase(client);
        caseService.closeCase(theCase.getId(), form(null, null));
        caseService.reopenCase(theCase.getId());

        ClientForm clientForm = new ClientForm();
        clientForm.setKeyword(client.getKeyword());
        clientForm.setWorkingRelationship("Angestellt");
        clientForm.setTargetGroup("Zielgruppe A");

        clientService.updateClient(client.getId(), clientForm);

        Case reloaded = caseRepo.findById(theCase.getId()).orElseThrow();
        assertThat(reloaded.getWorkingRelationship()).isEqualTo("Angestellt");
        assertThat(reloaded.getTargetGroup()).isEqualTo("Zielgruppe A");
    }

    @Test
    @Transactional
    @DisplayName("with several closed cases only the chosen one reopens")
    void shouldReopenOnlyTheChosenCase() {
        Client client = givenClient();
        Case older = givenCase(client, StatusService.STATUS_CLOSED,
                               LocalDate.now().minusYears(3), LocalDate.now().minusYears(2));
        Case newer = givenCase(client, StatusService.STATUS_CLOSED,
                               LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(1));

        caseService.reopenCase(older.getId());

        assertThat(caseRepo.findById(older.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusService.STATUS_OPEN);
        assertThat(caseRepo.findById(newer.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusService.STATUS_CLOSED);

        List<CaseDto> stillClosed = caseService.getCaseByClientIdAndStatus(
                client.getId(), StatusService.STATUS_CLOSED);
        assertThat(stillClosed).extracting(CaseDto::getId).containsExactly(newer.getId());
    }

}
