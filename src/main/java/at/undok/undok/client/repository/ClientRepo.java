package at.undok.undok.client.repository;

import at.undok.undok.client.model.entity.Client;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ClientRepo extends PagingAndSortingRepository<Client, UUID> {
    List<Client> findByKeyword(String keyword);
}
