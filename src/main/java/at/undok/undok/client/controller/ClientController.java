package at.undok.undok.client.controller;

import at.undok.undok.client.api.ClientApi;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.undok.client.service.ClientService;
import at.undok.undok.client.service.CounselingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
public class ClientController implements ClientApi {

    private final CounselingService counselingService;
    private final ClientService clientService;

    public ClientController(ClientService clientService, CounselingService counselingService) {
        this.clientService = clientService;
        this.counselingService = counselingService;
    }

    @Override
    public ResponseEntity createClient(ClientForm clientForm) {
        if (clientForm.getKeyword() == null) {
            return ResponseEntity.unprocessableEntity().body("Keyword must not be null");
        } else if (clientService.checkIfKeywordAlreadyExists(clientForm.getKeyword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Keyword already exists");
        } else {
            return ResponseEntity.ok(this.clientService.createClient(clientForm));
        }
    }

    @Override
    public Map<String, Map> getAllClients(int page, int size) {
        Map<String, Map> clients = this.clientService.getClients(page, size);
        return clients;
    }

    @Override
    public ClientDto getClientById(UUID id) {
        return clientService.getClientById(id);
    }

    @Override
    public CounselingDto createCounseling(UUID clientId, CounselingForm counselingForm) {
        return counselingService.createCounseling(clientId, counselingForm);
    }

    @Override
    public Long getNumberOfClients() {
        return clientService.getNumberOfClients();
    }

    @Override
    public ClientDto updateClient(UUID clientId, ClientDto clientDto) {
        return clientService.updateClient(clientId, clientDto);
    }

    @Override
    public List<AllClientDto> getAll() {
        return clientService.getAll();
    }
}
