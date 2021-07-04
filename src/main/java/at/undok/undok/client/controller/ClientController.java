package at.undok.undok.client.controller;

import at.undok.undok.client.api.ClientApi;
import at.undok.undok.client.model.dto.ClientCount;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.PersonDto;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
public class ClientController implements ClientApi {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
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

}
