package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployerRepo extends JpaRepository<Employer, UUID> {
}
