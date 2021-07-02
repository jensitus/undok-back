package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientRepo extends JpaRepository<Client, UUID> {
    List<Client> findByKeyword(String keyword);
}
