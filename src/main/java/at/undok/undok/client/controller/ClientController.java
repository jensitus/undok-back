package at.undok.undok.client.controller;

import at.undok.undok.client.api.ClientApi;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import at.undok.undok.client.service.ClientService;
import at.undok.undok.client.service.CounselingService;
import org.springframework.web.bind.annotation.RestController;

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
    public ClientDto createClient(ClientForm clientForm) {
        return this.clientService.createClient(clientForm);
    }

    @Override
    public Map<String, Map> getAllClients(int page, int size) {
        return this.clientService.getClients(page, size);
    }

    @Override
    public PersonDto getClientById(UUID id) {
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
    public ClientDto updateClient(UUID clientId, PersonDto personDto) {
        return clientService.updateClient(clientId, personDto);
    }

}
