package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.ClientEmployer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientEmployerRepo extends JpaRepository<ClientEmployer, UUID> {
    ClientEmployer findByEmployerIdAndClientId(UUID employerId, UUID clientId);

    List<ClientEmployer> findByClientId(UUID clientId);

    List<ClientEmployer> findByEmployerId(UUID employerId);
}
