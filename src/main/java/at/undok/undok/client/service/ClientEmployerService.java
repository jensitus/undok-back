package at.undok.undok.client.service;

import at.undok.undok.client.model.entity.ClientEmployer;
import at.undok.undok.client.model.entity.Employer;
import at.undok.undok.client.repository.ClientEmployerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientEmployerService {

    private final ClientEmployerRepo clientEmployerRepo;

    public boolean checkClientEmployer(UUID employerId, UUID clientId) {
        ClientEmployer byEmployerIdAndClientId = clientEmployerRepo.findByEmployerIdAndClientId(employerId, clientId);
        if (byEmployerIdAndClientId != null) {
            return true;
        } else {
            return false;
        }


    }

    public boolean addEmployerToClient(UUID employerId, UUID clientId) {
        ClientEmployer clientEmployer = new ClientEmployer();
        clientEmployer.setEmployerId(employerId);
        clientEmployer.setClientId(clientId);
        clientEmployer.setCreatedAt(LocalDateTime.now());
        clientEmployer.setUpdatedAt(LocalDateTime.now());
        try {
            clientEmployerRepo.save(clientEmployer);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean removeEmployerFromClient(UUID employerId, UUID clientId) {
        ClientEmployer clientEmployer = clientEmployerRepo.findByEmployerIdAndClientId(employerId, clientId);
        clientEmployerRepo.delete(clientEmployer);
        return true;
    }

    public List<ClientEmployer> getByClientId(UUID clientId) {
        return clientEmployerRepo.findByClientId(clientId);
    }

}
