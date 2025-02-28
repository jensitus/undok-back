package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CaseRepo extends JpaRepository<Case, UUID> {

    List<Case> findByClientIdAndStatus(UUID clientId, String status);

}
