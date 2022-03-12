package at.undok.undok.client.controller;

import at.undok.undok.client.api.ClientEmployerApi;
import at.undok.undok.client.model.form.ClientEmployerForm;
import at.undok.undok.client.service.ClientEmployerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientEmployerController implements ClientEmployerApi {

    private final ClientEmployerService clientEmployerService;

    @Override
    public boolean checkEmployerClient(UUID employerId, UUID clientId) {
        return clientEmployerService.checkClientEmployer(employerId, clientId);
    }

    @Override
    public boolean addEmployerToClient(UUID employerId, UUID clientId, ClientEmployerForm clientEmployerForm) {
        log.info(clientEmployerForm.toString());
        log.info(employerId.toString());
        log.info(clientId.toString());
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
