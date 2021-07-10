package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Counseling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CounselingRepo extends JpaRepository<Counseling, UUID> {



}
