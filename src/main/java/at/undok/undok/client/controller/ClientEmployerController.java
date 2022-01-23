package at.undok.undok.client.controller;

import at.undok.undok.client.api.ClientEmployerApi;
import at.undok.undok.client.model.form.ClientEmployerForm;
import at.undok.undok.client.service.ClientEmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ClientEmployerController implements ClientEmployerApi {

    private final ClientEmployerService clientEmployerService;

    @Override
    public boolean checkEmployerClient(UUID employerId, UUID clientId) {
        return clientEmployerService.checkClientEmployer(employerId, clientId);
    }

    @Override
    public boolean addEmployerToClient(UUID employerId, UUID clientId, ClientEmployerForm clientEmployerForm) {
        return clientEmployerService.addEmployerToClient(employerId, clientId, clientEmployerForm);
    }

    @Override
    public boolean removeEmployerFromClient(UUID employerId, UUID clientId) {
        return clientEmployerService.removeEmployerFromClient(employerId, clientId);
    }

    @Override
    public boolean updateClientEmployerJobDescription(UUID employerId, UUID clientId, ClientEmployerForm clientEmployerForm) {
        return clientEmployerService.updateClientEmployerJobDescription(employerId, clientId, clientEmployerForm);
    }
}
