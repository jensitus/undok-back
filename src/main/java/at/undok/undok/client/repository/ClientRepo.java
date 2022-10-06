package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepo extends JpaRepository<Client, UUID> {

    List<Client> findByKeyword(String keyword);

    boolean existsByKeyword(String keyword);

    List<Client> findByStatusOrderByCreatedAtDesc(String status);

    long countByStatus(String status);


}
