package at.undok.undok.client.repository;

import at.undok.undok.client.model.dto.CheckClientEmployerDto;
import at.undok.undok.client.model.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import java.util.List;
import java.util.UUID;

public interface EmployerRepo extends JpaRepository<Employer, UUID> {

    List<Employer> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);

    @Query(name = "checkRelationEmployerClient", nativeQuery = true)
    CheckClientEmployerDto checkActiveClient(UUID employerId, String status);

}
