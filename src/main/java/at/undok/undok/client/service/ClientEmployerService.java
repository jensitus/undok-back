package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.form.ClientEmployerForm;
import at.undok.undok.client.repository.ClientEmployerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientEmployerService {

    private final ClientEmployerRepo clientEmployerRepo;
    private final ClientService clientService;
    private final ModelMapper modelMapper;

    public boolean checkClientEmployer(UUID employerId, UUID clientId) {
        ClientEmployer byEmployerIdAndClientId = clientEmployerRepo.findByEmployerIdAndClientId(employerId, clientId);
        if (byEmployerIdAndClientId != null) {
            return true;
        } else {
            return false;
        }
    }

    public List<ClientDto> getClientsForEmployer(UUID employerId) {
        List<ClientEmployer> clientEmployers = clientEmployerRepo.findByEmployerId(employerId);
        List<ClientDto> clientDtos = new ArrayList<>();
        for (ClientEmployer ce : clientEmployers) {
            ClientDto clientDto = clientService.getClientById(ce.getClientId());
            clientDtos.add(clientDto);
        }
        return clientDtos;
    }

    public boolean addEmployerToClient(UUID employerId, UUID clientId, ClientEmployerForm clientEmployerForm) {
        ClientEmployer clientEmployer = new ClientEmployer();
        clientEmployer = setClientEmployer(clientEmployer, clientEmployerForm);
        clientEmployer.setCreatedAt(LocalDateTime.now());
        log.info(clientEmployer.toString());
        try {
            ClientEmployer save = clientEmployerRepo.save(clientEmployer);
            log.info("try catch savedClientEmployer.clientId" + save.getClientId());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean removeEmployerFromClient(UUID clientEmployerId, UUID clientId) {
        ClientEmployer clientEmployer = clientEmployerRepo.findById(clientEmployerId).orElseThrow();
        clientEmployerRepo.delete(clientEmployer);
        return true;
    }

    public List<ClientEmployer> getByClientId(UUID clientId) {
        return clientEmployerRepo.findByClientId(clientId);
    }

    public boolean updateClientEmployerJobDescription(UUID employerId, UUID clientId, ClientEmployerForm clientEmployerForm) {
        // ClientEmployer clientEmployer = clientEmployerRepo.findByEmployerIdAndClientId(employerId, clientId);
        ClientEmployer clientEmployer = clientEmployerRepo.findById(clientEmployerForm.getId()).orElseThrow();
        clientEmployer = setClientEmployer(clientEmployer, clientEmployerForm);
        clientEmployer.setUpdatedAt(LocalDateTime.now());
        clientEmployerRepo.save(clientEmployer);
        return true;
    }

    private ClientEmployer setClientEmployer(ClientEmployer clientEmployer, ClientEmployerForm clientEmployerForm) {
        clientEmployer.setEmployerId(clientEmployerForm.getEmployerId());
        clientEmployer.setClientId(clientEmployerForm.getClientId());
        clientEmployer.setFrom(clientEmployerForm.getFrom());
        clientEmployer.setUntil(clientEmployerForm.getUntil());
        clientEmployer.setIndustry(clientEmployerForm.getIndustry());
        clientEmployer.setIndustrySub(clientEmployerForm.getIndustrySub());
        clientEmployer.setJobRemarks(clientEmployerForm.getJobRemarks());
        clientEmployer.setJobFunction(clientEmployerForm.getJobFunction());
        return clientEmployer;
    }

}
