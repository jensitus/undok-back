package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployerRepo extends JpaRepository<Employer, UUID> {

    List<Employer> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);


}
